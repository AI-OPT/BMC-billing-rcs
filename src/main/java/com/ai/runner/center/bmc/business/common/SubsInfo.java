package com.ai.runner.center.bmc.business.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.runner.center.bmc.core.util.BmcException;
import com.ai.runner.center.bmc.core.util.CacheClient;

public class SubsInfo {
	private static Logger logger = LoggerFactory.getLogger(SubsInfo.class);
	private CacheClient cacheClient = CacheClient.getInstance();
	
	/**
	 * 查询subs_user缓存
	 * @param tenant_id
	 * @param data
	 * @throws BmcException
	 */
	public void setSubsUserData(String tenant_id, Map<String, String> data) throws BmcException{
		StringBuilder table = new StringBuilder();
		//table.append("subs_user").append(":").append(tenant_id);
		table.append("subs_user");
		Map<String, String> params = new HashMap<String, String>();
		params.put("service_num", data.get("service_num"));
		params.put("tenant_id", tenant_id);
		List<Map<String, String>> result = cacheClient.doQuery(table.toString(), params);
		if(result == null || result.size()==0){
			throw new BmcException("BMC-RULE0001B","subs_user表没有找到用户信息!");
		}
		data.put("subs_id", result.get(0).get("subs_id"));
		data.put("cust_id", result.get(0).get("cust_id"));
		data.put("acct_id", result.get(0).get("acct_id"));
	}
	
	/**
	 * 查询subs_comm缓存
	 * @param tenant_id
	 * @param data
	 * @throws BmcException
	 */
	public void setSubsCommData(String tenant_id, Map<String, String> data) throws BmcException{
		StringBuilder table = new StringBuilder();
		table.append("subs_user").append(":").append(tenant_id);
		Map<String, String> params = new HashMap<String, String>();
		String subs_id = data.get("subs_id");
		params.put("subs_id", subs_id);
		List<Map<String, String>> result = cacheClient.doQuery(table.toString(), params);
		if(result == null || result.size()==0){
			throw new BmcException("BMC-RULE0002B","subs_comm表没有找到用户信息!");
		}
		data.put("component_id", result.get(0).get("component_id"));
	}
	
}
