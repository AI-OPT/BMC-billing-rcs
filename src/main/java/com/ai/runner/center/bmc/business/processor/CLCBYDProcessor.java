package com.ai.runner.center.bmc.business.processor;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.ai.paas.ipaas.mcs.interfaces.ICacheClient;
import com.ai.runner.center.bmc.core.biz.api.ISysProcessor;
import com.ai.runner.center.bmc.core.util.BillingConstants;
import com.ai.runner.center.bmc.core.util.BmcException;
import com.ai.runner.center.bmc.core.util.DshmMcsClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class CLCBYDProcessor implements ISysProcessor {
//	private static Logger logger = LoggerFactory.getLogger(CLCBYDProcessor.class);
	private Gson gson = new Gson();

	@Override
	public void buildRule(Map<String, String> businessData) throws BmcException {
		String iccid = businessData.get("card_no");
		businessData.put("service_num", iccid);
		businessData.put("cal_type", "unit");

		ICacheClient cacheClient = DshmMcsClient.getClient();
		String value = cacheClient.hget("cache:hash:bl_userinfo", "service_num:tenant_id:"
				+ businessData.get("service_num") + ":" + businessData.get(BillingConstants.TENANT_ID));
		if (StringUtils.isEmpty(value)) {
			throw new BmcException("BMC-RULE0001B", "bl_userinfo表没有找到用户信息!");
		}
		JsonObject jsonObject = gson.fromJson("{" + value + "}", JsonObject.class);
		businessData.put("subs_id", jsonObject.getAsJsonPrimitive("subs_id").getAsString());
		businessData.put("cust_id", jsonObject.getAsJsonPrimitive("cust_id").getAsString());
		businessData.put("acct_id", jsonObject.getAsJsonPrimitive("acct_id").getAsString());
//		logger.info("subs_id====" + BUSINESSDATA.GET("SUBS_ID") + ";" + "CUST_ID====" + BUSINESSDATA.GET("CUST_ID")
//				+ ";" + "ACCT_ID====" + BUSINESSDATA.GET("ACCT_ID"));

		value = cacheClient.hget("cache:hash:cp_factor_info", "factor_value:tenant_id:"
				+ businessData.get("charging_station") + ":" + businessData.get(BillingConstants.TENANT_ID));
		if (StringUtils.isEmpty(value)) {
			throw new BmcException("BMC-RULE0010B", "cp_factor_info表没有找到参考因素项信息!");
		}
		jsonObject = gson.fromJson("{" + value + "}", JsonObject.class);
//		logger.info("factor_code=========" + jsonObject.getAsJsonPrimitive("factor_code").getAsString());
		value = cacheClient.hget("cache:hash:cp_unitprice_info",
				"factor_code:" + jsonObject.getAsJsonPrimitive("factor_code").getAsString());
		if (StringUtils.isEmpty(value)) {
			throw new BmcException("BMC-RULE0011B", "cp_unitprice_info表没有找到单价信息!");
		}
		jsonObject = gson.fromJson("{" + value + "}", JsonObject.class);
		businessData.put("fee_item_code", jsonObject.getAsJsonPrimitive("fee_item_code").getAsString());
//		logger.info("fee_item_code================" + businessData.get("fee_item_code"));
	}

	@Override
	public void calculate(Map<String, String> businessData) throws BmcException {

	}

}
