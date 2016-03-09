package com.ai.runner.center.bmc.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.ai.paas.ipaas.rcs.data.StreamData;
import com.ai.runner.center.bmc.core.persistence.entity.BmcRecordFmt.BmcRecordFmtKey;

/**
 * 对输入流和输出流报文封装
 * @author majun
 *
 */
public class PacketUtil {

	public final static String BUSINESS_ID = "business_id";
	public final static String SERVICE_ID = "service_id";
	public final static String TENANT_ID = "tenant_id";
	//public final static String PACKET_SERIAL_NUMBER = "psn";
	public final static String PACKET_CREATE_DATE = "create_date";
	public final static String PACKET_DATA = "data";
	
	private Map<String, String> header = new HashMap<String, String>();
	private Records records = null;
	private List<Map<String, String>> datas = null;
	private MappingRule[] mappingRules;//0:inputMappingRule  1:outMappingRule
	private Map<String, Integer> inIndexes;
	private Map<String, Integer> outIndexes;
	private String[] inputParams;
	private BmcRecordFmtKey bmcRecordFmtKey;
	
	private PacketUtil(String[] inputParams){
		System.out.println("inputParams[0]="+inputParams[0]);
		System.out.println("inputParams[1]="+inputParams[1]);
		System.out.println("inputParams[2]="+inputParams[2]);
		System.out.println("inputParams[3]="+inputParams[3]);
		System.out.println("inputParams[4]="+inputParams[4]);
		//System.out.println("inputParams[5]="+inputParams[5]);
		this.inputParams = inputParams;
	}
	
	private void init() throws BmcException{
		if(inputParams.length != 5){
			throw new BmcException("","input params is error!");
		}
//		if(mappingRules == null){
//			throw new BmcException("","MappingRule Object is null!");
//		}
		if(StringUtils.isBlank(inputParams[4])){
			throw new BmcException("","input data is null!");
		}
		setHeader();
		setData();
	}
	
	private void setHeader(){
		header.put(BUSINESS_ID, inputParams[0]);
		header.put(SERVICE_ID, inputParams[1]);
		header.put(TENANT_ID, inputParams[2]);
		//header.put(PACKET_SERIAL_NUMBER, inputParams[3]);
		header.put(PACKET_CREATE_DATE, inputParams[3]);
	}
	
	private void setData(){
		bmcRecordFmtKey = new BmcRecordFmtKey(getTenantId(), getServiceId());
		inIndexes = mappingRules[0].getIndexes(bmcRecordFmtKey);
		records = new Records(inputParams[4], inIndexes);
		datas = records.getData();
	}
	
	
	public List<Object> toStreamData(){
		outIndexes = mappingRules[1].getIndexes(bmcRecordFmtKey);
		List<Object> rtnValue = new ArrayList<Object>();
		rtnValue.add(getBusinessId());
		rtnValue.add(getServiceId());
		rtnValue.add(getTenantId());
		//rtnValue.add(getPacketSN());
		rtnValue.add(getCreateDate());
		rtnValue.add(toDataString());
		return rtnValue;
	}
	
	
	private String toDataString(){
		StringBuilder out = new StringBuilder();
		for(Map<String,String> tmpData:datas){
			String[] tmpArr = new String[outIndexes.size()];
			for(Entry<String,Integer> entry:outIndexes.entrySet()){
				tmpArr[entry.getValue()]=StringUtils.defaultString(tmpData.get(entry.getKey()));
			}
			StringBuilder record = new StringBuilder();
			for(String e:tmpArr){
				record.append(e).append(BillingConstants.FIELD_SPLIT);
			}
			out.append(record.substring(0, record.length()-1)).append(BillingConstants.RECORD_SPLIT);
		}
		return out.substring(0, out.length()-1);
	}
	
	public boolean dataIsBlank(){
		if(datas == null){
			return true;
		}
		if(datas.size()==0){
			return true;
		}else{
			return false;
		}
	}
	
	public void setMappingRule(MappingRule[] mappingRules) {
		this.mappingRules = mappingRules;
	}

	public String getBusinessId(){
		return header.get(BUSINESS_ID);
	}
	
	public String getServiceId(){
		return header.get(SERVICE_ID);
	}
	
	public String getTenantId(){
		return header.get(TENANT_ID);
	}
	
//	public String getPacketSN(){
//		return header.get(PACKET_SERIAL_NUMBER);
//	}
	
	public String getCreateDate(){
		return header.get(PACKET_CREATE_DATE);
	}
	
	public List<Map<String, String>> getData() {
		return datas;
	}
	
	public Map<String, String> getPacketHeader(){
		return header;
	}
	
	public int getDataSize(){
		return datas.size();
	}
	
	public static PacketUtil parseObject(String original, MappingRule[] mappingRules) throws BmcException{
		if(StringUtils.isBlank(original)){
			throw new BmcException("","input String is null!");
		}
		String[] inputParams = StringUtils.splitPreserveAllTokens(original,",");
		PacketUtil packetUtil = new PacketUtil(inputParams);
		packetUtil.setMappingRule(mappingRules);
		packetUtil.init();
		return packetUtil;
	}
	
	public static PacketUtil parseObject(StreamData streamData, MappingRule[] mappingRules) throws BmcException{
		String[] inputParams = new String[5];
		inputParams[0] = streamData.getString(0);
		inputParams[1] = streamData.getString(1);
		inputParams[2] = streamData.getString(2);
		inputParams[3] = streamData.getString(3);
		inputParams[4] = streamData.getString(4);
		//inputParams[5] = streamData.getString(5);
		PacketUtil packetUtil = new PacketUtil(inputParams);
		packetUtil.setMappingRule(mappingRules);
		packetUtil.init();
		return packetUtil;
	}

}
