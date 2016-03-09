package com.ai.runner.center.bmc.core.flow.cost.rule;

import java.math.BigDecimal;

import com.ai.runner.center.bmc.core.flow.cost.entity.ChargingDetailRecord;

/**
 * 批价接口
 * 
 * @author bixy
 *
 */
public interface IRateFinder {

	/**
	 * 根据记录获取批价
	 * 
	 * @param chargingDetailRecord
	 * @return
	 */
	public BigDecimal getRate(ChargingDetailRecord chargingDetailRecord);

	/**
	 * 获取一个最低的费率
	 * 
	 * @return
	 */
	public BigDecimal getCheapestRate();

	/**
	 * 设置批价信息
	 * 
	 * @param price
	 * @param value
	 */
	public void setExtValue(BigDecimal price, String value);

	/**
	 * 获取批价类型
	 * 
	 * @return
	 */
	public String getRateFinderType();
}
