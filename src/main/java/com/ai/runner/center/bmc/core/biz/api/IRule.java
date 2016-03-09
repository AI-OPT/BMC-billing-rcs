package com.ai.runner.center.bmc.core.biz.api;

import java.util.Map;

import com.ai.runner.center.bmc.core.util.BmcException;

public interface IRule {
	
	//void getRuleModel(String jsonData);
	
	void buildRule(Map<String, String> businessData) throws BmcException;
	
}
