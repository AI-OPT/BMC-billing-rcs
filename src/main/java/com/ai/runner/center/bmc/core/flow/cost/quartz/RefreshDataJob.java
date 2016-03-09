package com.ai.runner.center.bmc.core.flow.cost.quartz;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ai.runner.center.bmc.core.flow.cost.billingto.BillingToException;
import com.ai.runner.center.bmc.core.flow.cost.billingto.IBillingTo;
import com.ai.runner.center.bmc.core.flow.cost.container.BillingContainer;
import com.ai.runner.center.bmc.core.flow.cost.container.ContainerProxy;
import com.ai.runner.center.bmc.core.flow.cost.entity.AccuFieldValue;
import com.ai.runner.center.bmc.core.flow.cost.entity.ChargingDetailRecord;
import com.ai.runner.center.bmc.core.flow.cost.entity.GroupFieldValue;
import com.ai.runner.center.bmc.core.output.HbaseOutputProcessor;
import com.ai.runner.center.bmc.core.output.MDSOutputProcessor;

public class RefreshDataJob implements Job {
	private static Logger logger = Logger.getLogger(RefreshDataJob.class);

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		ContainerProxy containerProxy = (ContainerProxy) dataMap.get(ContainerProxy.class.getName());
		IBillingTo billingTo = (IBillingTo) dataMap.get(IBillingTo.class.getName());
		MDSOutputProcessor mdsOutputProcessor = (MDSOutputProcessor) dataMap.get(MDSOutputProcessor.class.getName());
		HbaseOutputProcessor hbaseOutputProcessor = (HbaseOutputProcessor) dataMap.get(HbaseOutputProcessor.class.getName());
		if (containerProxy.isNeedToFlush()) {
			flush(containerProxy.getContainer(), billingTo, mdsOutputProcessor, hbaseOutputProcessor);
		}
	}

	private void flush(BillingContainer billingContainer, IBillingTo billingTo, MDSOutputProcessor mdsOutputProcessor, HbaseOutputProcessor hbaseOutputProcessor) {
		Map<GroupFieldValue, AccuFieldValue> data = billingContainer.fetchContainerMap();
		List<ChargingDetailRecord> detailRecords = billingContainer.fetchSourceRecords();
		if (CollectionUtils.isNotEmpty(detailRecords)) {
			long startFlush = System.currentTimeMillis();
			try {
				billingTo.sumBill(data);

				// 输出详单

//				mdsOutputProcessor.execute(detailRecords);
				hbaseOutputProcessor.execute(detailRecords);
			} catch (BillingToException e) {
				// 放弃处理，不入详单库
				logger.error(e);
			}
			long endFlush = System.currentTimeMillis();
			logger.info("flush cost time is " + (endFlush - startFlush) + "ms");
		}
	}
}
