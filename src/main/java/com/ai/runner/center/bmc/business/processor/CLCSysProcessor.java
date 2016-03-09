package com.ai.runner.center.bmc.business.processor;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.runner.center.bmc.business.common.BlUserInfo;
import com.ai.runner.center.bmc.business.common.CLCDataInfo;
import com.ai.runner.center.bmc.core.biz.api.ISysProcessor;
import com.ai.runner.center.bmc.core.util.BmcException;

public class CLCSysProcessor implements ISysProcessor {

	private static Logger logger = LoggerFactory.getLogger(CLCSysProcessor.class);
	//private SubsInfo subsInfo = new SubsInfo();
	private BlUserInfo blUserInfo = new BlUserInfo();
	private CLCDataInfo clcDataInfo = new CLCDataInfo();
	
	@Override
	public void buildRule(Map<String, String> businessData) throws BmcException {
		String iccid = businessData.get("card_no");
		businessData.put("service_num", iccid);
		//String tenant_id = businessData.get(RecordUtil.TENANT_ID);
		//subsInfo.setSubsUserData(tenant_id, businessData);
		//businessData.put("subs_id", businessData.get("subs_id"));
		blUserInfo.setBlUserData(businessData);
		clcDataInfo.setUnitpriceInfo(businessData);

		businessData.put("cal_type", "unit");
		//System.out.println(businessData.toString());
	}

	@Override
	public void calculate(Map<String, String> businessData) throws BmcException {
	}

}
