package com.ai.runner.center.bmc.core.flow.cost.rule;

import java.util.List;
import java.util.Map;

import com.ai.runner.center.bmc.core.flow.cost.entity.ChargingDetailRecord;
import com.ai.runner.center.bmc.core.flow.cost.entity.FeeTypeAndSubject;

/**
 * 抽象的计费过程
 * 
 * @author bixy
 *
 */
public abstract class AbstractBilling implements IBilling {

	@Override
	public List<ChargingDetailRecord> billing(ChargingDetailRecord record) {
		return calculate(getRate(record), record);
	}

	/**
	 * 获取批价信息
	 * 
	 * @param record
	 * @return
	 */
	public abstract Map<FeeTypeAndSubject, IRateFinder> getRate(ChargingDetailRecord record);

	/**
	 * 计算费用
	 * 
	 * @param rateInfo
	 * @param record
	 * @return 不允许返回null值，可以返回空集合
	 */
	public abstract List<ChargingDetailRecord> calculate(Map<FeeTypeAndSubject, IRateFinder> rateInfo, ChargingDetailRecord record);
}
