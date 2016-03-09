package com.ai.runner.center.bmc.core.flow.cost.resourcebook;

import com.ai.runner.center.bmc.core.flow.cost.entity.ChargingDetailRecord;

/**
 * 访问资源账本
 * 
 * @author bixy
 * 
 */
public interface IAccessResourceBook {
	public static final String ACCESS_RESOURCEBOOK = "billing.access.resourcebook";
	public static final String ACCESS_RESOURCEBOOK_PARAM = "billing.access.resourcebook.param";

	public void reduce(String msg, ChargingDetailRecord chargingDetailRecord);

}
