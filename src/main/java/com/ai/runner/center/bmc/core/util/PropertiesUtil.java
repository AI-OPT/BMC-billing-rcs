package com.ai.runner.center.bmc.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PropertiesUtil {

	private static Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);
	private ConcurrentHashMap<String, String> dataMap = new ConcurrentHashMap<String, String>();
	private static PropertiesUtil instance = new PropertiesUtil();
	
	public static void load(String resourcesName){
//		String resFileName = resourcesMapping.get(resourcesName);
//		if(StringUtils.isNotBlank(resFileName)){
//			loadResources(resFileName);
//		}
		if(StringUtils.isBlank(resourcesName)){
			return;
		}
		StringBuffer resPath = new StringBuffer(resourcesName);
		if(!resourcesName.endsWith(".properties")){
			resPath.append(".properties");
		}
		loadResources(resPath.toString());
	}
	
	public static String getValue(String key) {
		String value = instance.dataMap.get(key);
		return value;
	}

	public static Object remove(String k) {
		Object v = instance.dataMap.remove(k);
		return v;
	}
	
	private static void loadResources(String fileName){
		Properties properties = new Properties();
		InputStream inputStream = null;
		try{
			//inputStream = PropertiesUtil.class.getResourceAsStream("/"+fileName);
			inputStream = PropertiesUtil.class.getClassLoader().getResourceAsStream(fileName);
			properties.load(inputStream);
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(inputStream!=null){
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		Iterator<Entry<Object, Object>> itor = properties.entrySet().iterator();
		while (itor.hasNext()) {
			Entry<Object, Object> entry = itor.next();
			String name = (String) entry.getKey();
			String value = (String) entry.getValue();
			instance.dataMap.put(name, value);
		}
		logger.debug("load resources "+fileName);
		
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//PropertiesUtil.load("res-reader");
		//PropertiesUtil.load("res-processor");
		PropertiesUtil.load("bmc-processor");
		PropertiesUtil.load("bmc-flow.properties");
		String str = PropertiesUtil.getValue("test.abc");
		String stra = PropertiesUtil.getValue("deds.ws.server.wsdl");
		System.out.println("str="+str);
		System.out.println("stra="+stra);
	}
	
	
	

}
