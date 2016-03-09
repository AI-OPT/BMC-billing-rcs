package com.ai.runner.center.bmc.core.flow.cost.billingto.impl;

import java.util.Map;

import com.ai.runner.center.bmc.core.flow.cost.billingto.BillingToException;
import com.ai.runner.center.bmc.core.flow.cost.billingto.IBillingTo;
import com.ai.runner.center.bmc.core.flow.cost.entity.AccuFieldValue;
import com.ai.runner.center.bmc.core.flow.cost.entity.GroupFieldValue;

public class BillingToRedis implements IBillingTo {

	@Override
	public void sumBill(Map<GroupFieldValue, AccuFieldValue> data) throws BillingToException {
	}

	@Override
	public Map<GroupFieldValue, AccuFieldValue> getFinished() {
		return null;
	}

}
