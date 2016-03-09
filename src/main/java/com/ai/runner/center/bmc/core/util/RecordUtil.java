package com.ai.runner.center.bmc.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.ai.paas.ipaas.rcs.data.StreamData;
import com.ai.runner.center.bmc.core.persistence.entity.BmcRecordFmt.BmcRecordFmtKey;

public class RecordUtil {
	public final static String SYSTEM_ID = "system_id";
	public final static String SERVICE_ID = "service_id";
	public final static String TENANT_ID = "tenant_id";
	public final static String BATCH_SERIAL_NUMBER = "psn";
	public final static String SERIAL_NUMBER = "sn";
	//public final static String CREATE_DATE = "create_date";
	public final static String RECORD_DATA = "data";
	
	//private Map<String, String> header;
	private String[] orderHeadKeys;
	private Records records = null;
	private Map<String, String> data = new HashMap<String, String>();
	private MappingRule[] mappingRules;//0:inputMappingRule  1:outMappingRule
	private Map<String, Integer> inIndexes;
	private Map<String, Integer> outIndexes;
	private String inputData;
	private BmcRecordFmtKey bmcRecordFmtKey;
	
	private RecordUtil(String original, MappingRule[] mappingRules,String[] orderHeadKeys){
		String[] inputParams = StringUtils.splitPreserveAllTokens(original,BillingConstants.FIELD_SPLIT);
		data.put(SYSTEM_ID, inputParams[0]);
		data.put(TENANT_ID, inputParams[1]);
		data.put(SERVICE_ID, inputParams[2]);
		data.put(BATCH_SERIAL_NUMBER, inputParams[3]);
		data.put(SERIAL_NUMBER, inputParams[4]);
		StringBuilder strData = new StringBuilder();
		for(int i=5;i<inputParams.length;i++){
			strData.append(inputParams[i]).append(BillingConstants.FIELD_SPLIT);
		}
		strData.delete(strData.length()-1, strData.length());
		this.inputData = strData.toString();
		this.mappingRules = mappingRules;
		this.orderHeadKeys = orderHeadKeys;
	}
	
	private RecordUtil(StreamData streamData, MappingRule[] mappingRules, String[] orderHeadKeys){
		for(String headKey:orderHeadKeys){
			if(streamData.contains(headKey)){
				data.put(headKey, streamData.getStringByField(headKey));
			}
		}
		this.inputData = streamData.getStringByField(RECORD_DATA);
		this.mappingRules = mappingRules;
		this.orderHeadKeys = orderHeadKeys;
	}
	
	private void init() throws BmcException{
		bmcRecordFmtKey = new BmcRecordFmtKey(getTenantId(), getServiceId());
		inIndexes = mappingRules[0].getIndexes(bmcRecordFmtKey);
		records = new Records(inputData, inIndexes);
		Map<String, String> recordData = records.getData().get(0);
		if(recordData != null){
			data.putAll(recordData);
		}
	}
	
	public List<Object> toStreamData(){
		outIndexes = mappingRules[1].getIndexes(bmcRecordFmtKey);
		List<Object> rtnValue = new ArrayList<Object>();
		for(String headKey:orderHeadKeys){
			rtnValue.add(data.get(headKey));
		}
		rtnValue.add(toDataString());
		return rtnValue;
	}
	
	
	private String toDataString(){
		//for(Map<String,String> tmpData:datas){
		String[] tmpArr = new String[outIndexes.size()];
		for(Entry<String,Integer> entry:outIndexes.entrySet()){
			tmpArr[entry.getValue()]=StringUtils.defaultString(data.get(entry.getKey()));
		}
		StringBuilder record = new StringBuilder();
		for(String e:tmpArr){
			record.append(e).append(BillingConstants.FIELD_SPLIT);
		}
		record.delete(record.length()-1, record.length());
		return record.toString();
	}
	
	public boolean dataIsBlank(){
		if(data == null){
			return true;
		}
		if(data.size()==0){
			return true;
		}else{
			return false;
		}
	}
	
	public void setMappingRule(MappingRule[] mappingRules) {
		this.mappingRules = mappingRules;
	}

	public String getSystemId(){
		return data.get(SYSTEM_ID);
	}
	
	public String getServiceId(){
		return data.get(SERVICE_ID);
	}
	
	public String getTenantId(){
		return data.get(TENANT_ID);
	}
	
	public Map<String, String> getData() {
		return data;
	}
	
//	public Map<String, String> getPacketHeader(){
//		return header;
//	}

	
	public static RecordUtil parseObject(String original, MappingRule[] mappingRules) throws BmcException{
		if(StringUtils.isBlank(original)){
			throw new BmcException("","input String is null!");
		}
		String[] orderHeadKeys = new String[]{SYSTEM_ID,TENANT_ID,SERVICE_ID,BATCH_SERIAL_NUMBER,SERIAL_NUMBER};
		RecordUtil recordUtil = new RecordUtil(original,mappingRules,orderHeadKeys);
		recordUtil.init();
		return recordUtil;
	}
	
	
	public static RecordUtil parseObject(StreamData streamData, String[] orderHeadKeys, MappingRule[] mappingRules) throws BmcException{
		RecordUtil recordUtil = new RecordUtil(streamData,mappingRules,orderHeadKeys);
		recordUtil.init();
		return recordUtil;
	}
	
	public static String[] getOutputNames(String processorName){
		List<String> outputs = new ArrayList<String>();
		outputs.add(RecordUtil.SYSTEM_ID);
		outputs.add(RecordUtil.TENANT_ID);
		outputs.add(RecordUtil.SERVICE_ID);
		outputs.add(RecordUtil.BATCH_SERIAL_NUMBER);
		outputs.add(RecordUtil.SERIAL_NUMBER);
		if(processorName.equals(BillingConstants.RULE_ADAPT_PROCESSOR)){
			outputs.add(BillingConstants.SUBS_ID);
		}
		outputs.add(RecordUtil.RECORD_DATA);
		return outputs.toArray(new String[outputs.size()]);
	}
	
	public static String[] getHeadKeys(String processorName){
		List<String> headkeys = new ArrayList<String>();
		headkeys.add(RecordUtil.SYSTEM_ID);
		headkeys.add(RecordUtil.TENANT_ID);
		headkeys.add(RecordUtil.SERVICE_ID);
		headkeys.add(RecordUtil.BATCH_SERIAL_NUMBER);
		headkeys.add(RecordUtil.SERIAL_NUMBER);
		if(processorName.equals(BillingConstants.RULE_ADAPT_PROCESSOR)){
			headkeys.add(BillingConstants.SUBS_ID);
		}
		return headkeys.toArray(new String[headkeys.size()]);
	}
	
	
}
