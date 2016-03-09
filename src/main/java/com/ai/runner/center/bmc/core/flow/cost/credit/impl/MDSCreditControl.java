package com.ai.runner.center.bmc.core.flow.cost.credit.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.paas.ipaas.mds.IMessageSender;
import com.ai.paas.ipaas.mds.MsgSenderFactory;
import com.ai.paas.ipaas.uac.vo.AuthDescriptor;
import com.ai.runner.center.bmc.core.flow.cost.credit.ICreditControl;
import com.ai.runner.center.bmc.core.flow.cost.entity.ChargingDetailRecord;
import com.ai.runner.center.bmc.core.flow.cost.entity.MDSParam;
import com.ai.runner.center.bmc.core.util.BillingConstants;
import com.google.gson.Gson;

/**
 * MDS 输出信控
 * 
 * @author bixy
 * 
 */
public class MDSCreditControl implements ICreditControl {
	private static Logger logger = LoggerFactory.getLogger(MDSCreditControl.class);
	private IMessageSender msgSender;
	private int partition;
	private Gson jsonConverter = new Gson();

	public MDSCreditControl(String param) {
		logger.info("credit mds param is " + param);
		MDSParam paramObj = jsonConverter.fromJson(param, MDSParam.class);
		AuthDescriptor ad = new AuthDescriptor(paramObj.getAuthAddr(), paramObj.getpId(), paramObj.getPassword(), paramObj.getSrvId());
		this.msgSender = MsgSenderFactory.getClient(ad, paramObj.getTopic());
		this.partition = paramObj.getPartition();
	}

	@Override
	public void send(String msg, ChargingDetailRecord chargingDetailRecord) {
		msgSender.send(msg.getBytes(), (chargingDetailRecord.getBmcRecordFmtKey().hashCode() + chargingDetailRecord.get(BillingConstants.SUBS_ID).hashCode()) % partition);
	}

}
