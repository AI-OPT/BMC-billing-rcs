package com.ai.runner.center.bmc.core.flow;

import java.io.Serializable;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.paas.ipaas.rcs.bolt.Processor;
import com.ai.paas.ipaas.rcs.common.Fields;
import com.ai.paas.ipaas.rcs.common.FlowContext;
import com.ai.paas.ipaas.rcs.common.ProcessorCollector;
import com.ai.paas.ipaas.rcs.data.StreamData;
import com.ai.runner.center.bmc.core.executor.FailBillDeliver;
import com.ai.runner.center.bmc.core.init.FailBillStartup;
import com.ai.runner.center.bmc.core.util.BillingConstants;
import com.ai.runner.center.bmc.core.util.BmcException;
import com.ai.runner.center.bmc.core.util.JdbcProxy;
import com.ai.runner.center.bmc.core.util.MappingRule;
import com.ai.runner.center.bmc.core.util.RecordUtil;

public class UnpackingHandler extends Processor implements Serializable{

	private static final long serialVersionUID = -2752509101540238587L;
	private static Logger logger = LoggerFactory.getLogger(UnpackingHandler.class);
	private MappingRule[] mappingRules = new MappingRule[2];
//	private String[] outputName=new String[]{RecordUtil.SYSTEM_ID,RecordUtil.TENANT_ID,RecordUtil.SERVICE_ID,
//			RecordUtil.BATCH_SERIAL_NUMBER,RecordUtil.SERIAL_NUMBER,RecordUtil.RECORD_DATA};
	private String[] outputName = RecordUtil.getOutputNames(BillingConstants.UNPACKING_PROCESSOR);
	
	private ProcessorCollector collector;
	
	@Override
	public void prepare(Map aConf, FlowContext aContext, ProcessorCollector collector) {
		System.out.println("UnpackingHandler================================");
		JdbcProxy.loadResource(aConf);
		FailBillStartup.start();
		mappingRules[0] = MappingRule.getMappingRule(MappingRule.FORMAT_TYPE_INPUT);
		mappingRules[1] = mappingRules[0];
		this.collector = collector;
	}
	
	@Override
	public void execute(StreamData adata) {
		String line = "";
		try {
			line = adata.getString(0);
			System.out.println("UnpackingHandler.data---->>>>"+line);
			String[] inputDatas = StringUtils.splitPreserveAllTokens(line, BillingConstants.RECORD_SPLIT);
			RecordUtil packetUtil = null;
			for(String inputData:inputDatas){
				packetUtil = RecordUtil.parseObject(inputData, mappingRules);
				if(!packetUtil.dataIsBlank()){
					super.setValues(packetUtil.toStreamData());
				}
			}
		}catch (BmcException be) {
			logger.error("context",be);
			be.printStackTrace();
		}catch(Exception e){
			logger.error("context",e);
			FailBillDeliver.deliver(adata, e, BillingConstants.UNPACKING_PROCESSOR);
		}finally{
			super.ack();
		}

	}


	@Override
	public Fields getOutFields() {
		return new Fields(outputName);
	}
	
	@Override
	public void buildLogger(Logger arg0) {
		this.LOG = arg0;
	}

	@Override
	public void cleanup() {
	
	}
	
	
}
