package com.ai.runner.center.bmc.core.flow.cost.resourcebook.impl;

import com.ai.paas.ipaas.mds.IMessageSender;
import com.ai.paas.ipaas.mds.MsgSenderFactory;
import com.ai.paas.ipaas.uac.vo.AuthDescriptor;
import com.ai.runner.center.bmc.core.flow.cost.entity.ChargingDetailRecord;
import com.ai.runner.center.bmc.core.flow.cost.entity.MDSParam;
import com.ai.runner.center.bmc.core.flow.cost.resourcebook.IAccessResourceBook;
import com.ai.runner.center.bmc.core.util.BillingConstants;
import com.google.gson.Gson;

/**
 * 通过mds进行资源扣减
 * 
 * @author bixy
 * 
 */
public class MDSAccessResourceBook implements IAccessResourceBook {
	private IMessageSender msgSender;
	private int partition;

	public MDSAccessResourceBook(String param) {
		MDSParam paramObj = (new Gson()).fromJson(param, MDSParam.class);
		AuthDescriptor ad = new AuthDescriptor(paramObj.getAuthAddr(), paramObj.getpId(), paramObj.getPassword(), paramObj.getSrvId());
		this.msgSender = MsgSenderFactory.getClient(ad, paramObj.getTopic());
		this.partition = paramObj.getPartition();
	}

	@Override
	public void reduce(String msg, ChargingDetailRecord chargingDetailRecord) {
		msgSender.send(msg.getBytes(), (chargingDetailRecord.getBmcRecordFmtKey().hashCode() + chargingDetailRecord.get(BillingConstants.SUBS_ID).hashCode()) % partition);
	}
}
