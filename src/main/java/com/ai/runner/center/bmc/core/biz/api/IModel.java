package com.ai.runner.center.bmc.core.biz.api;

import java.util.Map;

import com.ai.runner.center.bmc.core.util.BmcException;

public interface IModel {

	void calculate(Map<String, String> businessData) throws BmcException;
	
}
