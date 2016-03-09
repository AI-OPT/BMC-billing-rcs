package com.ai.runner.center.bmc.core.flow.cost.resourcebook.impl;

import org.apache.log4j.Logger;

import com.ai.runner.center.bmc.core.flow.cost.entity.ChargingDetailRecord;
import com.ai.runner.center.bmc.core.flow.cost.resourcebook.IAccessResourceBook;

public class PrintAccessResourceBook implements IAccessResourceBook {
	private static Logger logger = Logger.getLogger(PrintAccessResourceBook.class);

	@Override
	public void reduce(String msg, ChargingDetailRecord chargingDetailRecord) {
		logger.info("reduce ChargingDetailRecord " + chargingDetailRecord.getBmcRecordFmtKey());
	}

}
