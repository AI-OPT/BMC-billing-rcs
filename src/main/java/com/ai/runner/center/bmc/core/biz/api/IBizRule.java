package com.ai.runner.center.bmc.core.biz.api;


import java.util.Map;

import com.ai.runner.center.bmc.core.util.BmcException;

/**
 * 业务规则接口
 * @author majun
 *
 */
public interface IBizRule{
	
	void initParameter(String doType) throws BmcException;
	
	void buildRule(Map<String, String> data) throws BmcException;
	
	void calculate(Map<String, String> data) throws BmcException;
	
	public enum do_type {
		rule, calculate
    }
}
