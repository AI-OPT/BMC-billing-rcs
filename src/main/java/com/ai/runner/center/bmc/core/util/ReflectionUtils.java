package com.ai.runner.center.bmc.core.util;

import com.ai.runner.center.bmc.core.biz.api.IBizRule;
import com.ai.runner.center.bmc.core.biz.api.ISysProcessor;

public class ReflectionUtils {

	private ReflectionUtils(){
	}
	
	public static IBizRule getBusinessRuleObject(String strClass) throws BmcException{
		Class<IBizRule> ruleObj = null;
		try {
			ruleObj = (Class<IBizRule>)Class.forName(strClass);
			return ruleObj.newInstance();
		} catch (ClassNotFoundException e) {
			throw new BmcException("",e);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new BmcException("",e);
		}
	}
	
	public static ISysProcessor getSysProcessorObj(String strClass) throws BmcException{
		Class<ISysProcessor> clazz = null;
		try{
			clazz = (Class<ISysProcessor>)Class.forName(strClass);
			return clazz.newInstance();
		}catch(Exception e){
			throw new BmcException("",e);
		}
	}

}
