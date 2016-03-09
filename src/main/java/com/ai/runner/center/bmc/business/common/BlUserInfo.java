package com.ai.runner.center.bmc.business.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.runner.center.bmc.core.util.BmcException;
import com.ai.runner.center.bmc.core.util.CacheClient;
import com.ai.runner.center.bmc.core.util.RecordUtil;

public class BlUserInfo {

	private static Logger logger = LoggerFactory.getLogger(BlUserInfo.class);
	private CacheClient cacheClient = CacheClient.getInstance();
	
	public void setBlUserData(Map<String, String> data) throws BmcException{
		String table = "bl_userinfo";
		Map<String, String> params = new TreeMap<String, String>();
		params.put("service_num", data.get("service_num"));
		params.put("tenant_id", data.get(RecordUtil.TENANT_ID));
		//params.put("system_id", data.get(RecordUtil.SYSTEM_ID));
		List<Map<String, String>> result = cacheClient.doQuery(table, params);
		if(result == null || result.size()==0){
			throw new BmcException("BMC-RULE0001B","bl_userinfo表没有找到用户信息!");
		}
		data.put("subs_id", result.get(0).get("subs_id"));
		data.put("cust_id", result.get(0).get("cust_id"));
		data.put("acct_id", result.get(0).get("acct_id"));
	}
	
}
