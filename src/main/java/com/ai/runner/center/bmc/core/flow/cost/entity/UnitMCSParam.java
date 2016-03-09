package com.ai.runner.center.bmc.core.flow.cost.entity;

import java.math.BigDecimal;

import com.ai.runner.center.bmc.core.util.BillingConstants;

/**
 * unit 计费中 mcs缓存的数据结构
 * <li>状态</li>
 * <li>用电量</li>
 * <li>电费</li>
 * <li>关键区间点</li>
 * <li>当前区间点</li>
 * 
 * @author bixy
 *
 */
public class UnitMCSParam {
	// private Integer status;
	private BigDecimal powerConsumption;
	private BigDecimal fee;
	private String keyPoint;
	private String currentPoint;

	public UnitMCSParam(String value) {
		String[] fields = value.split(BillingConstants.FIELD_SPLIT, -1);
		// this.status = Integer.parseInt(fields[0]);
		powerConsumption = new BigDecimal(fields[0]);
		fee = new BigDecimal(fields[1]);
		this.keyPoint = fields[2];
		this.currentPoint = fields[3];
	}

	public UnitMCSParam(BigDecimal powerConsumption, BigDecimal fee, String keyPoint, String currentPoint) {
		// this.status = status;
		this.powerConsumption = powerConsumption;
		this.fee = fee;
		this.keyPoint = keyPoint;
		this.currentPoint = currentPoint;
	}

	// public Integer getStatus() {
	// return status;
	// }
	//
	// public void setStatus(Integer status) {
	// this.status = status;
	// }

	public String getKeyPoint() {
		return keyPoint;
	}

	public void setKeyPoint(String keyPoint) {
		this.keyPoint = keyPoint;
	}

	public String value() {
		return powerConsumption.doubleValue() + BillingConstants.FIELD_SPLIT + fee.doubleValue() + BillingConstants.FIELD_SPLIT + keyPoint + BillingConstants.FIELD_SPLIT + currentPoint;
	}

	public BigDecimal getPowerConsumption() {
		return powerConsumption;
	}

	public void setPowerConsumption(BigDecimal powerConsumption) {
		this.powerConsumption = powerConsumption;
	}

	public BigDecimal getFee() {
		return fee;
	}

	public void setFee(BigDecimal fee) {
		this.fee = fee;
	}

	public String getCurrentPoint() {
		return currentPoint;
	}

	public void setCurrentPoint(String currentPoint) {
		this.currentPoint = currentPoint;
	}

}
