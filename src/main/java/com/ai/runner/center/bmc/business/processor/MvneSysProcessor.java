package com.ai.runner.center.bmc.business.processor;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.runner.center.bmc.business.common.PriceInfo;
import com.ai.runner.center.bmc.business.common.SubsInfo;
import com.ai.runner.center.bmc.core.biz.api.IBizRule;
import com.ai.runner.center.bmc.core.biz.api.ISysProcessor;
import com.ai.runner.center.bmc.core.util.BmcException;
import com.ai.runner.center.bmc.core.util.RecordUtil;
import com.ai.runner.center.bmc.core.util.ReflectionUtils;

/**
 * MVNE业务处理器
 * @author majun
 *
 */
public class MvneSysProcessor implements ISysProcessor {

	private static Logger logger = LoggerFactory.getLogger(MvneSysProcessor.class);
	private SubsInfo subsInfo = new SubsInfo();
    private PriceInfo priceInfo = new PriceInfo();
	
	@Override
	public void buildRule(Map<String, String> businessData) throws BmcException{
		
		//对协议中数据逐条进行处理
		//for(Map<String, String> data:businessData){
		String tenant_id = businessData.get(RecordUtil.TENANT_ID);
		subsInfo.setSubsUserData(tenant_id, businessData);
		subsInfo.setSubsCommData(tenant_id, businessData);
		//System.out.println("MvneSysProcessor="+businessData.toString());
		priceInfo.setPriceDetailData(businessData);
		String className = businessData.get("class");
		IBizRule iBizRule = ReflectionUtils.getBusinessRuleObject(className);
		iBizRule.initParameter(IBizRule.do_type.rule.name());
		iBizRule.buildRule(businessData);
		//}
		
	}
	
	@Override
	public void calculate(Map<String, String> businessData) throws BmcException{
		
		//for(Map<String, String> data:businessData){
		String className = businessData.get("class");
		IBizRule iBizRule = ReflectionUtils.getBusinessRuleObject(className);
		iBizRule.initParameter(IBizRule.do_type.calculate.name());
		iBizRule.calculate(businessData);
	
		//}
		
	}


}
