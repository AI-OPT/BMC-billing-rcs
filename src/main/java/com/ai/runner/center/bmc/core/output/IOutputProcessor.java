package com.ai.runner.center.bmc.core.output;

import java.util.List;

import com.ai.runner.center.bmc.core.flow.cost.entity.ChargingDetailRecord;

public interface IOutputProcessor {
	public static final String BILLING_OUTPUT_PARAM = "billing.output.param";
	void execute(List<ChargingDetailRecord> detailRecords);
	
}
