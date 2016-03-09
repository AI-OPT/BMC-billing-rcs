package com.ai.runner.center.bmc.core.flow.cost.rule.impl;

import java.util.List;
import java.util.Map;

import com.ai.runner.center.bmc.core.flow.cost.entity.ChargingDetailRecord;
import com.ai.runner.center.bmc.core.flow.cost.entity.FeeTypeAndSubject;
import com.ai.runner.center.bmc.core.flow.cost.rule.AbstractBilling;
import com.ai.runner.center.bmc.core.flow.cost.rule.IBilling;
import com.ai.runner.center.bmc.core.flow.cost.rule.IRateFinder;

/**
 * 阶梯式计费
 * 
 * @author bixy
 * 
 */
public class Step extends AbstractBilling implements IBilling {

	@Override
	public Map<FeeTypeAndSubject, IRateFinder> getRate(ChargingDetailRecord record) {
		throw new UnsupportedOperationException();
	}

	@Override
	public List<ChargingDetailRecord> calculate(Map<FeeTypeAndSubject, IRateFinder> rateInfo, ChargingDetailRecord record) {
		throw new UnsupportedOperationException();
		// return null;
	}

}
