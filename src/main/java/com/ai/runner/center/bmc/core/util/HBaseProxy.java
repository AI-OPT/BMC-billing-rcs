package com.ai.runner.center.bmc.core.util;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class HBaseProxy {
	private static Logger logger = LoggerFactory.getLogger(HBaseProxy.class);
	private static Connection connection;
	
	public static void loadResource(Map<String,String> config){
		Configuration configuration = HBaseConfiguration.create();
		String hbaseSite = config.get(BillingConstants.BILLING_HBASE_PARAM);
		try {
			if(StringUtils.isBlank(hbaseSite)){
				throw new Exception("输入参数中没有配置hbase.site属性信息!");
			}
			JsonParser jsonParser = new JsonParser();
			JsonObject jsonObject = (JsonObject)jsonParser.parse(hbaseSite);
			for(Entry<String, JsonElement> entry:jsonObject.entrySet()){
				configuration.set(entry.getKey(), entry.getValue().getAsString());
			}
			connection = ConnectionFactory.createConnection(configuration);
		} catch (Exception e) {
			logger.error("error", e);
		}
	}
	
	public static Connection getConnection(){
		return connection;
	}
	

//	public static void main(String[] args) {
//		JSONObject jsonObject = new JSONObject();
//		jsonObject.put("hbase.zookeeper.property.clientPort", "2181");
//		jsonObject.put("hbase.zookeeper.quorum", "node01,node02,node03");
//		jsonObject.put("hbase.master", "node01");
//		
//		JSONObject jsonObj = new JSONObject();
//		jsonObj.put("hbase.site", jsonObject.toJSONString());
//		
//		System.out.println(jsonObj.toJSONString());
//		
//	}
	
	
}
