package com.ai.runner.center.bmc.core.flow.format;

import java.io.Serializable;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.paas.ipaas.rcs.bolt.Processor;
import com.ai.paas.ipaas.rcs.common.Fields;
import com.ai.paas.ipaas.rcs.common.FlowContext;
import com.ai.paas.ipaas.rcs.common.ProcessorCollector;
import com.ai.paas.ipaas.rcs.data.StreamData;
import com.ai.runner.center.bmc.core.biz.api.IDataQuality;
import com.ai.runner.center.bmc.core.dataquality.DataQualityCache;
import com.ai.runner.center.bmc.core.executor.FailBillDeliver;
import com.ai.runner.center.bmc.core.init.FailBillStartup;
import com.ai.runner.center.bmc.core.util.BillingConstants;
import com.ai.runner.center.bmc.core.util.BmcException;
import com.ai.runner.center.bmc.core.util.HBaseProxy;
import com.ai.runner.center.bmc.core.util.JdbcProxy;
import com.ai.runner.center.bmc.core.util.MappingRule;
import com.ai.runner.center.bmc.core.util.RecordUtil;

/**
 * 数据质量处理器，对输入数据进行过滤与校验
 * @author majun
 *
 */
public class DataQualityHandler extends Processor implements Serializable{

	private static final long serialVersionUID = -5461858426588237133L;
	private static Logger logger = LoggerFactory.getLogger(DataQualityHandler.class);
	private IDataQuality dataQuality;
	private MappingRule[] mappingRules = new MappingRule[2];
	private ProcessorCollector collector;
//	private String[] orderHeadKeys= new String[]{RecordUtil.SYSTEM_ID,RecordUtil.TENANT_ID,RecordUtil.SERVICE_ID,
//			RecordUtil.BATCH_SERIAL_NUMBER,RecordUtil.SERIAL_NUMBER};
//	private String[] outputName= new String[]{RecordUtil.SYSTEM_ID,RecordUtil.TENANT_ID,RecordUtil.SERVICE_ID,
//			RecordUtil.BATCH_SERIAL_NUMBER,RecordUtil.SERIAL_NUMBER,RecordUtil.RECORD_DATA};
	private String[] orderHeadKeys=RecordUtil.getHeadKeys(BillingConstants.DATA_QUALITY_PROCESSOR);
	private String[] outputName=RecordUtil.getOutputNames(BillingConstants.DATA_QUALITY_PROCESSOR);
	
	
	@Override
	public void prepare(Map aConf, FlowContext aContext, ProcessorCollector collector) {
		JdbcProxy.loadResource(aConf);
		HBaseProxy.loadResource(aConf);
		FailBillStartup.start();
		mappingRules[0] = MappingRule.getMappingRule(MappingRule.FORMAT_TYPE_INPUT);
		mappingRules[1] = MappingRule.getMappingRuleBefore(MappingRule.FORMAT_TYPE_OUTPUT);
		DataQualityCache.getInstance();
		System.out.println("DataQualityHandler================================");
		//以后换成可配置的方式
		dataQuality = new GeneralDataQuality();
		this.collector = collector;
	}

	@Override
	public void execute(StreamData adata) {
		try {
			RecordUtil packetUtil = RecordUtil.parseObject(adata, orderHeadKeys, mappingRules);
			logger.debug("[DataQualityHandler|execute|business_id]"+packetUtil.getSystemId());
			System.out.println("[DataQualityHandler|execute|business_id]"+packetUtil.getSystemId());
			//System.out.println("+++++++"+packetUtil.getData());
			processTask(packetUtil);
			if(!packetUtil.dataIsBlank()){
				super.setValues(packetUtil.toStreamData());
			}
		}catch (BmcException be) {
			logger.error("context",be);
			//be.printStackTrace();
			FailBillDeliver.deliver(adata, be, BillingConstants.DATA_QUALITY_PROCESSOR);
		}catch(Exception e){
			logger.error("context",e);
			FailBillDeliver.deliver(adata, e, BillingConstants.DATA_QUALITY_PROCESSOR);
		}finally{
			super.ack();
		}
	}
	
	private void processTask(RecordUtil packetUtil){
		Map<String, String> businessData = packetUtil.getData();
		try{
			dataQuality.checkData(businessData);
			dataQuality.formatData(businessData);
		}catch(BmcException e){
			logger.error(e.getMessage());
			FailBillDeliver.deliver(businessData, e, BillingConstants.DATA_QUALITY_PROCESSOR);
			packetUtil.getData().clear();
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
