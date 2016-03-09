package com.ai.runner.center.bmc.business.common;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.runner.center.bmc.core.util.BmcException;
import com.ai.runner.center.bmc.core.util.CacheClient;
import com.ai.runner.center.bmc.core.util.RecordUtil;

public class CLCDataInfo {

	private static Logger logger = LoggerFactory.getLogger(CLCDataInfo.class);
	private CacheClient cacheClient = CacheClient.getInstance();
	
	public void setUnitpriceInfo(Map<String, String> data) throws BmcException{
		Map<String, String> params = new TreeMap<String, String>();
		params.put("factor_value", data.get("charging_station"));
		//params.put("system_id", data.get(RecordUtil.SYSTEM_ID));
		params.put("tenant_id", data.get(RecordUtil.TENANT_ID));
		List<Map<String, String>> result = cacheClient.doQuery("cp_factor_info", params);
		if(result == null || result.size()==0){
			throw new BmcException("BMC-RULE0010B","cp_factor_info表没有找到参考因素项信息!");
		}
		String factor_code = result.get(0).get("factor_code");
		
		params = new TreeMap<String, String>();
		params.put("factor_code", factor_code);
		result = cacheClient.doQuery("cp_unitprice_info", params);
		if(result == null || result.size()==0){
			throw new BmcException("BMC-RULE0011B","cp_unitprice_info表没有找到单价信息!");
		}
		data.put("fee_item_code", result.get(0).get("fee_item_code"));
	}
	
}
