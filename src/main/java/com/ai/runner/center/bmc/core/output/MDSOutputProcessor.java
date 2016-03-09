package com.ai.runner.center.bmc.core.output;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.ai.paas.ipaas.mds.IMessageSender;
import com.ai.paas.ipaas.mds.MsgSenderFactory;
import com.ai.paas.ipaas.uac.vo.AuthDescriptor;
import com.ai.runner.center.bmc.core.entity.JdbcParam;
import com.ai.runner.center.bmc.core.exception.BillingPrepareException;
import com.ai.runner.center.bmc.core.flow.cost.RecordMappingRule;
import com.ai.runner.center.bmc.core.flow.cost.entity.ChargingDetailRecord;
import com.ai.runner.center.bmc.core.flow.cost.entity.MDSParam;
import com.ai.runner.center.bmc.core.util.BillingConstants;
import com.ai.runner.center.bmc.core.util.JdbcTemplate;
import com.google.gson.Gson;

public class MDSOutputProcessor implements IOutputProcessor {
	private IMessageSender msgSender;
	private RecordMappingRule mappingRule;
	private int partition;
	private Gson jsonConverter = new Gson();

	public MDSOutputProcessor(String param, JdbcParam jdbcParam) {
		Connection conn = null;
		try {
			conn = JdbcTemplate.getConnection(jdbcParam);
		} catch (ClassNotFoundException e) {
			throw new BillingPrepareException(e);
		} catch (SQLException e) {
			throw new BillingPrepareException(e);
		}
		mappingRule = RecordMappingRule.getMappingRuleBefore(RecordMappingRule.FORMAT_TYPE_OUTPUT, conn);
		MDSParam paramObj = jsonConverter.fromJson(param, MDSParam.class);
		AuthDescriptor ad = new AuthDescriptor(paramObj.getAuthAddr(), paramObj.getpId(), paramObj.getPassword(), paramObj.getSrvId());
		this.msgSender = MsgSenderFactory.getClient(ad, paramObj.getTopic());
		this.partition = paramObj.getPartition();
	}

	@Override
	public void execute(List<ChargingDetailRecord> detailRecords) {
		int increase = 0;
		for (ChargingDetailRecord chargingDetailRecord : detailRecords) {
			Map<String, Integer> indexes = mappingRule.getIndexes(chargingDetailRecord.getBmcRecordFmtKey());
			Map<String, String> fields = chargingDetailRecord.getFields();
			StringBuilder msg = new StringBuilder();
			msg.append(chargingDetailRecord.getBmcRecordFmtKey().toString());
			msg.append(BillingConstants.COMMON_SPLIT);
			for (String key : indexes.keySet()) {
				msg.append(fields.get(key) + BillingConstants.FIELD_SPLIT);
			}
			msg.deleteCharAt(msg.length() - 1);
			msgSender.send(msg.toString(), increase % partition);
			increase++;
		}
	}

}
