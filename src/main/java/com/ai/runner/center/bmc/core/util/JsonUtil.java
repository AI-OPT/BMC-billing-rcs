package com.ai.runner.center.bmc.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Deprecated
public class JsonUtil {
	
	public final static String BUSINESS_ID = "business_id";
	public final static String BUSINESS_TYPE = "business_type";
	public final static String PACKET_SERIAL_NUMBER = "psn";
	public final static String PACKET_CREATE_DATE = "create_date";
	public final static String PACKET_DATA = "data";
	
	private String jsonString;
	private Map<String, String> header = new HashMap<String, String>();
	private List<Map<String, String>> datas = new ArrayList<Map<String, String>>();

	
	private JsonUtil(String jsonString){
		this.jsonString = jsonString;
	}
	
	private void init() throws Exception{
		if(StringUtils.isBlank(jsonString)){
			throw new Exception("Json String is null!");
		}
		JsonParser jsonParser = new JsonParser();
		JsonObject jsonObject = (JsonObject)jsonParser.parse(jsonString);
		
		setHeader(jsonObject);
		setData(jsonObject);
	}
	
	private void setHeader(JsonObject jsonObject){
		header.put(BUSINESS_ID, jsonObject.get(BUSINESS_ID).getAsString());
		header.put(BUSINESS_TYPE, jsonObject.get(BUSINESS_TYPE).getAsString());
		header.put(PACKET_SERIAL_NUMBER, jsonObject.get(PACKET_SERIAL_NUMBER).getAsString());
		header.put(PACKET_CREATE_DATE, jsonObject.get(PACKET_CREATE_DATE).getAsString());
	}
	
	private void setData(JsonObject jsonObject){
		JsonObject jsonObj = null;
		JsonArray jsonArray = jsonObject.get(PACKET_DATA).getAsJsonArray();
		for(Iterator<JsonElement> it=jsonArray.iterator();it.hasNext();){
			jsonObj = (JsonObject)it.next();
			Map<String, String> record = new HashMap<String, String>();
			for (Iterator<Entry<String,JsonElement>> iter = jsonObj.entrySet().iterator(); iter.hasNext();) {
				Entry<String,JsonElement> entry= iter.next();
				String key = entry.getKey();
				record.put(key, jsonObj.get(key).getAsString());
			}
			datas.add(record);
		}
	}
	
	public static JsonUtil loadJson(String json) throws Exception{
		JsonUtil jsonUtil = new JsonUtil(json);
		jsonUtil.init();
		return jsonUtil;
	}
	
	public String getBusinessId(){
		return header.get(BUSINESS_ID);
	}
	
	public String getBusinessType(){
		return header.get(BUSINESS_TYPE);
	}
	
	public String getPacketSN(){
		return header.get(PACKET_SERIAL_NUMBER);
	}
	
	public String getCreateDate(){
		return header.get(PACKET_CREATE_DATE);
	}

	public List<Map<String, String>> getData() {
		return datas;
	}
	
	public String toJsonString(){
		JsonObject outJsonObj = new JsonObject();
		outJsonObj.addProperty(BUSINESS_ID, getBusinessId());
		outJsonObj.addProperty(BUSINESS_TYPE, getBusinessType());
		outJsonObj.addProperty(PACKET_SERIAL_NUMBER, getPacketSN());
		outJsonObj.addProperty(PACKET_CREATE_DATE, getCreateDate());
		
		JsonArray outJsonArray = new JsonArray();
		JsonObject jsonObjTemp;
		for(Map<String, String> data:datas){
			jsonObjTemp = new JsonObject();
			for(Iterator<Entry<String, String>> iter=data.entrySet().iterator();iter.hasNext();){
				Entry<String, String> entry = iter.next();
				jsonObjTemp.addProperty(entry.getKey(), entry.getValue());
			}
			outJsonArray.add(jsonObjTemp);
		}
		outJsonObj.add(PACKET_DATA, outJsonArray);
		return outJsonObj.toString();
	}

}