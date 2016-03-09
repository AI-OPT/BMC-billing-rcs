package com.ai.runner.center.bmc.core.flow.cost.rule;

import java.util.List;

import com.ai.runner.center.bmc.core.flow.cost.entity.ChargingDetailRecord;

/**
 * 计费规则接口
 * 
 * @author bixy
 * 
 */
public interface IBilling {

	/**
	 * 计算详单记录，这里返回一个列表<br/>
	 * 原因是计算的详单在某些情况下可能被挂起(由于详单的计算是需要先后顺序的)<br/>
	 * 所以可能返回一个空的list（此时详单被挂起）。也有可能返回多个结果，原因是被挂起的详单在此次计算中解挂被计算掉。<br/>
	 * 
	 * @param record
	 * @return 如果详单被挂起，则返回一个空的list，不返回null值
	 */
	public List<ChargingDetailRecord> billing(ChargingDetailRecord record);
}
