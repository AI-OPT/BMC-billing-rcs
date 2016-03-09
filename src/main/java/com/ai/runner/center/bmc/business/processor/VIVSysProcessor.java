package com.ai.runner.center.bmc.business.processor;

import java.util.Map;

import com.ai.runner.center.bmc.business.common.SubsInfo;
import com.ai.runner.center.bmc.core.biz.api.ISysProcessor;
import com.ai.runner.center.bmc.core.util.BmcException;
import com.ai.runner.center.bmc.core.util.RecordUtil;

public class VIVSysProcessor implements ISysProcessor {

	private SubsInfo subsInfo = new SubsInfo();
	
	@Override
	public void buildRule(Map<String, String> businessData) throws BmcException {
		String tenant_id = businessData.get(RecordUtil.TENANT_ID);
		subsInfo.setSubsUserData(tenant_id, businessData);
		businessData.put("cal_type", "package");
		businessData.put("down_stream", "0");
	}

	@Override
	public void calculate(Map<String, String> businessData) throws BmcException {
	}

}
