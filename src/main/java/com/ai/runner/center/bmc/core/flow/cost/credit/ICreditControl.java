package com.ai.runner.center.bmc.core.flow.cost.credit;

import com.ai.runner.center.bmc.core.flow.cost.entity.ChargingDetailRecord;

/**
 * 发送信控接口
 * 
 * @author bixy
 * 
 */
public interface ICreditControl {
	public static final String CREDIT_CONTROL = "billing.credit.control";
	public static final String CREDIT_CONTROL_PARAM = "billing.credit.control.param";

	public void send(String msg,ChargingDetailRecord chargingDetailRecord);
}
