package com.ai.runner.center.bmc.core.flow.cost.billingto;

import java.util.Map;

import com.ai.runner.center.bmc.core.flow.cost.entity.AccuFieldValue;
import com.ai.runner.center.bmc.core.flow.cost.entity.GroupFieldValue;

/**
 * 累帐目标接口
 * 
 * @author bixy
 * 
 */
public interface IBillingTo {

	public void sumBill(Map<GroupFieldValue, AccuFieldValue> data) throws BillingToException;
	
	public Map<GroupFieldValue, AccuFieldValue> getFinished();
}
