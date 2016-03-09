package com.ai.runner.center.bmc.business.common;

import java.math.BigDecimal;

public class PriceCalculator {

	
	public static String calculateGSM(String usage,String unit,String price){
		BigDecimal bUsage = new BigDecimal(usage);
		BigDecimal bUnit = new BigDecimal(unit);
		BigDecimal bPrice = new BigDecimal(price);
		return bUsage.divide(bUnit, 10, BigDecimal.ROUND_HALF_UP).multiply(bPrice).setScale(2, BigDecimal.ROUND_HALF_UP).toString();
	}
	
	
	
}
