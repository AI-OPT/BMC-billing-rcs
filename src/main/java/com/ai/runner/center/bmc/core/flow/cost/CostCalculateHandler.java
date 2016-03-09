package com.ai.runner.center.bmc.core.flow.cost;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.ai.paas.ipaas.rcs.common.Fields;
import com.ai.paas.ipaas.rcs.common.FlowContext;
import com.ai.paas.ipaas.rcs.common.ProcessorCollector;
import com.ai.paas.ipaas.rcs.data.StreamData;
import com.ai.runner.center.bmc.core.entity.JdbcParam;
import com.ai.runner.center.bmc.core.flow.EmptyProcessor;
import com.ai.runner.center.bmc.core.flow.cost.billingto.BillingToException;
import com.ai.runner.center.bmc.core.flow.cost.billingto.IBillingTo;
import com.ai.runner.center.bmc.core.flow.cost.container.BillingContainer;
import com.ai.runner.center.bmc.core.flow.cost.container.ContainerProxy;
import com.ai.runner.center.bmc.core.flow.cost.entity.AccuFieldValue;
import com.ai.runner.center.bmc.core.flow.cost.entity.ChargingDetailRecord;
import com.ai.runner.center.bmc.core.flow.cost.entity.GroupFieldValue;
import com.ai.runner.center.bmc.core.flow.cost.quartz.RefreshDataJob;
import com.ai.runner.center.bmc.core.output.HbaseOutputProcessor;
import com.ai.runner.center.bmc.core.output.IOutputProcessor;
import com.ai.runner.center.bmc.core.output.MDSOutputProcessor;
import com.ai.runner.center.bmc.core.util.HbaseOutputInfos;

/**
 * 费用计算处理器
 * 
 * @author bixy
 * 
 */
public class CostCalculateHandler extends EmptyProcessor implements Serializable {
	private static Logger logger = Logger.getLogger(CostCalculateHandler.class);
	public static final String FLUSH_METRIC = "billing.flush.time.metric";
	public static final String BILLING_DESTINATION = "billing.destination";
	private IBillingTo billingTo; // 计费结果输出
	private IOutputProcessor hbaseOutputProcessor;
	private IOutputProcessor mdsOutputProcessor;
	private Scheduler scheduler;
	private ContainerProxy containerProxy;

	public void prepare(Map conf, FlowContext aContext, ProcessorCollector collector) {
		logger.debug("==================[CostCalculateHandler]-->>" + Thread.currentThread().getName());

		billingTo = CostFactory.getBillingDestination(conf);
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			JdbcParam jdbcParam = JdbcParam.getInstance(conf);
			AccountReriodRuleManager accountReriodRuleManager = new AccountReriodRuleManager(scheduler, jdbcParam);
			int flushMetric = getFlushMetric(conf);
			this.containerProxy = new ContainerProxy(conf, flushMetric, accountReriodRuleManager);
			HbaseOutputInfos hbaseOutputInfos = new HbaseOutputInfos(JdbcParam.getInstance(conf));
			this.hbaseOutputProcessor = new HbaseOutputProcessor(hbaseOutputInfos, conf);
			this.mdsOutputProcessor = new MDSOutputProcessor((String) conf.get(IOutputProcessor.BILLING_OUTPUT_PARAM), jdbcParam);
			createQuartzJob(flushMetric);

			// 启动定时任务
			scheduler.start();
		} catch (SchedulerException e) {
			logger.error(e);
		}
	}

	public void execute(StreamData streamData) {
		logger.info("[CostCalculateHandler|execute|data]---->>>>" + streamData);
		containerProxy.pushToStatistic(streamData);
		if (containerProxy.isNeedToFlush()) {
			long startFlush = System.currentTimeMillis();
			flush(containerProxy.getContainer());
			long endFlush = System.currentTimeMillis();
			logger.debug("flush cost time is " + (endFlush - startFlush) + "ms");
		}
		super.ack();
	}

	/**
	 * 刷新容器数据
	 * 
	 * @param billingContainer
	 */
	private void flush(BillingContainer billingContainer) {
		Map<GroupFieldValue, AccuFieldValue> data = billingContainer.fetchContainerMap();
		List<ChargingDetailRecord> detailRecords = billingContainer.fetchSourceRecords();
		logger.debug("flush size is " + detailRecords.size());
		if (CollectionUtils.isNotEmpty(detailRecords)) {
			try {
				billingTo.sumBill(data);

				// 输出详单

//				mdsOutputProcessor.execute(detailRecords);
				hbaseOutputProcessor.execute(detailRecords);
			} catch (BillingToException e) {
				// 放弃处理，不入详单库
				logger.error(e);
			}
		}
	}

	/**
	 * 开始定时任务，共两个<br/>
	 * 第一个：每个月的1号0点0分进入切换期(根据系统时间)<br/>
	 * 第二个：每个月的1号某时（不超过24小时），按小时计，关闭切换期<br/>
	 * 
	 * @param accountPeriodRule
	 * @param switchHours
	 * @throws SchedulerException
	 */
	private void createQuartzJob(Integer flushMetric) throws SchedulerException {
		// 创建定时刷新容器任务,根据用户定义的刷新时长进行不断的刷新
		JobDataMap refreshContainerDataMap = new JobDataMap();
		refreshContainerDataMap.put(ContainerProxy.class.getName(), this.containerProxy);
		refreshContainerDataMap.put(IBillingTo.class.getName(), this.billingTo);
		refreshContainerDataMap.put(MDSOutputProcessor.class.getName(), this.mdsOutputProcessor);
		refreshContainerDataMap.put(HbaseOutputProcessor.class.getName(), this.hbaseOutputProcessor);
		JobDetail refreshContainerJobDetail = JobBuilder.newJob(RefreshDataJob.class).usingJobData(refreshContainerDataMap).build();
		int flushSec = (flushMetric / 1000 == 0) ? 1 : (flushMetric / 1000);
		Trigger refreshContainerTrigger = TriggerBuilder.newTrigger().withSchedule(SimpleScheduleBuilder.repeatSecondlyForever(flushSec)).build();
		scheduler.scheduleJob(refreshContainerJobDetail, refreshContainerTrigger);

	}

	private int getFlushMetric(Map conf) {
		String flushMetricStr = (String) conf.get(FLUSH_METRIC);
		int flushMetric = 1000;
		if (StringUtils.isEmpty(flushMetricStr) || Integer.parseInt(flushMetricStr) <= 0)
			logger.warn("billing.flush.time.metric is not set ,use default 1000ms");
		else
			flushMetric = Integer.parseInt(flushMetricStr);
		return flushMetric;
	}

	public Fields getOutFields() {
		return new Fields("jsonParam");
	}

}
