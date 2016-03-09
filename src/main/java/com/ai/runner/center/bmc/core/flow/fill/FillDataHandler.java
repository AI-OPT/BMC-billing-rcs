package com.ai.runner.center.bmc.core.flow.fill;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.paas.ipaas.rcs.common.Fields;
import com.ai.paas.ipaas.rcs.common.FlowContext;
import com.ai.paas.ipaas.rcs.common.ProcessorCollector;
import com.ai.paas.ipaas.rcs.data.StreamData;
import com.ai.runner.center.bmc.core.biz.BizFactory;
import com.ai.runner.center.bmc.core.biz.api.ISysProcessor;
import com.ai.runner.center.bmc.core.executor.FailBillDeliver;
import com.ai.runner.center.bmc.core.flow.EmptyProcessor;
import com.ai.runner.center.bmc.core.init.FailBillStartup;
import com.ai.runner.center.bmc.core.util.BillingConstants;
import com.ai.runner.center.bmc.core.util.BmcException;
import com.ai.runner.center.bmc.core.util.DshmMcsClient;
import com.ai.runner.center.bmc.core.util.JdbcProxy;
import com.ai.runner.center.bmc.core.util.MappingRule;
import com.ai.runner.center.bmc.core.util.RecordUtil;

public class FillDataHandler extends EmptyProcessor {
	private static Logger logger = LoggerFactory.getLogger(FillDataHandler.class);
	private BizFactory bizFactory;
	private MappingRule[] mappingRules = new MappingRule[2];
	private String[] orderHeadKeys = RecordUtil.getHeadKeys(BillingConstants.RULE_ADAPT_PROCESSOR);
	private String[] outputName = RecordUtil.getOutputNames(BillingConstants.RULE_ADAPT_PROCESSOR);

	@Override
	public void prepare(Map aConf, FlowContext aContext, ProcessorCollector collector) {
		JdbcProxy.loadResource(aConf);
		DshmMcsClient.createClient(aConf);
		FailBillStartup.start();
		mappingRules[0] = MappingRule.getMappingRuleBefore(MappingRule.FORMAT_TYPE_OUTPUT);
		mappingRules[1] = mappingRules[0];
		System.out.println("FillDataHandler================================");
		bizFactory = new BizFactory();
	}

	@Override
	public void execute(StreamData adata) {
		try {
			RecordUtil packetUtil = RecordUtil.parseObject(adata, orderHeadKeys, mappingRules);
			logger.debug("[RuleAdaptHandler|execute|business_id]" + packetUtil.getSystemId());
			System.out.println(packetUtil.getData());
			ISysProcessor processor = bizFactory.getProcessor(packetUtil.getTenantId(),packetUtil.getServiceId());
			processTask(processor, packetUtil);
			if (!packetUtil.dataIsBlank()) {
				super.setValues(packetUtil.toStreamData());
			}
		} catch (BmcException be) {
			logger.error("context", be);
			FailBillDeliver.deliver(adata, be, BillingConstants.RULE_ADAPT_PROCESSOR);
		} catch (Exception e) {
			logger.error("context", e);
			FailBillDeliver.deliver(adata, e, BillingConstants.RULE_ADAPT_PROCESSOR);
		} finally {
			super.ack();
		}
	}

	private void processTask(ISysProcessor processor, RecordUtil packetUtil) {
		Map<String, String> businessData = packetUtil.getData();
		try {
			processor.buildRule(businessData);
		} catch (BmcException e) {
			logger.error("", e);
			// 错单处理
			FailBillDeliver.deliver(businessData, e, BillingConstants.RULE_ADAPT_PROCESSOR);
			packetUtil.getData().clear();
		}
	}
	
	@Override
	public Fields getOutFields() {
		return new Fields(outputName);
	}
}
