package com.ai.runner.center.bmc.core.flow.cost.credit.impl;

import org.apache.log4j.Logger;

import com.ai.runner.center.bmc.core.flow.cost.credit.ICreditControl;
import com.ai.runner.center.bmc.core.flow.cost.entity.ChargingDetailRecord;

public class PrintCreditControl implements ICreditControl {
	private static Logger logger = Logger.getLogger(PrintCreditControl.class);

	@Override
	public void send(String msg, ChargingDetailRecord chargingDetailRecord) {
		logger.info("send ChargingDetailRecord " + chargingDetailRecord.getBmcRecordFmtKey());
	}
}
