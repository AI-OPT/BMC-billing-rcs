package com.ai.runner.center.bmc.business.rule;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.runner.center.bmc.business.common.PriceCalculator;
import com.ai.runner.center.bmc.business.common.PriceInfo;
import com.ai.runner.center.bmc.core.biz.api.IBizRule;
import com.ai.runner.center.bmc.core.util.BmcException;
import com.ai.runner.center.bmc.core.util.CacheClient;

public class VoiceUnitRule implements IBizRule {
	
	private static Logger logger = LoggerFactory.getLogger(VoiceUnitRule.class);
	private CacheClient cacheClient = CacheClient.getInstance();
	//private PriceInfo priceInfo = new PriceInfo();

	@Override
	public void initParameter(String doType) throws BmcException {
		if(cacheClient == null){
			logger.error("cache client connection is null!");
		}
	}

	@Override
	public void buildRule(Map<String, String> data) throws BmcException{
		logger.debug("【VoiceUnitRule-buildRule+++++++++++++++++】");
		PriceInfo priceInfo = new PriceInfo();
		priceInfo.setUnitpriceInfoData(data);
		//判断协议中是否存在参考因素
		if(StringUtils.isNotBlank(data.get("factor_code"))){
			//找到匹配条件的参考因素
			if(!priceInfo.matchingFactor(data)){
				//logger.debug("+++++++++参考因素比对不正确需要进入错单处理！");
				throw new BmcException("","参考因素比对不正确需要进入错单处理!");
			}
		}
		priceInfo.setUnitpriceItemData(data);
	}

	@Override
	public void calculate(Map<String, String> data) throws BmcException{
		logger.debug("【VoiceUnitRule-calculate-----------------】");
		String cost = "";
		String usage = data.get("call_duration");
		String unitType = data.get("unit_type");
		String priceValue = data.get("price_value");
		if(StringUtils.isNotBlank(usage)&&StringUtils.isNotBlank(unitType)&&StringUtils.isNotBlank(priceValue)){
			cost = PriceCalculator.calculateGSM(usage, unitType, priceValue);
		}
		data.put("cost", cost);
	}

	

}
