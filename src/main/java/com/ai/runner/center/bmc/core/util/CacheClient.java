package com.ai.runner.center.bmc.core.util;

import java.util.List;
import java.util.Map;

import com.ai.runner.center.dshm.api.dshmservice.interfaces.IdshmreadSV;

public class CacheClient {

	private String cache_ip = "10.1.234.164";
	private String cache_port = "8686";
	private IdshmreadSV service;
	private static CacheClient cacheClient;
	public final static String delimiter = "#";
	
//	static{
//		cacheClient = new CacheClient();
//		try {
//			cacheClient.init();
//		} catch (Exception e) {
//			cacheClient = null;
//			e.printStackTrace();
//		}
//	}
	
	private CacheClient(){
	}
	
	public static void loadResource(Map<String,String> config){
		cacheClient = new CacheClient();
		try {
			cacheClient.init(config);
		} catch (Exception e) {
			cacheClient = null;
			e.printStackTrace();
		}
	}
	
	public static CacheClient getInstance(){
		return cacheClient;
	}
	
	private void init(Map<String,String> config) throws Exception{
		//cache_ip = (String)PropertiesUtil.getValue("bmc.flow.cache.ip");
		//cache_port = (String)PropertiesUtil.getValue("bmc.flow.cache.port");
		//service = (IdshmreadSV) ServiceRegiter.registerService(cache_ip, cache_port, ShmConstants.ShmServiceCode.SHM_SERVICE_CODE);
		cache_ip = config.get("bmc.flow.cache.ip");
		cache_port = config.get("bmc.flow.cache.port");
		service = (IdshmreadSV) ServiceRegiter.registerService(cache_ip, cache_port, ShmConstants.ShmServiceCode.SHM_SERVICE_CODE);	
	}
	
	public List<Map<String, String>> doQuery(String tableName, Map<String,String> params) throws BmcException{
		if(cacheClient == null){
			throw new BmcException("","cache client connection is null!");
		}
		List<Map<String, String>> result = null;
		try {
			System.out.println("[cache tableName]="+tableName);
			System.out.println("[cache params]="+params.toString());
			result = service.list(tableName).where(params).executeQuery();
		} catch (Exception e) {
			throw new BmcException("", e.getMessage(),e);
		}
		return result;
	}
	
	
	
	
	
}
