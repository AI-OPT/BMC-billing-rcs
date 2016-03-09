package com.ai.runner.center.bmc.core.flow.cost.rule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ai.runner.center.bmc.core.flow.cost.entity.ChargingDetailRecord;
import com.ai.runner.center.bmc.core.flow.cost.rule.impl.Package;
import com.ai.runner.center.bmc.core.flow.cost.rule.impl.Step;
import com.ai.runner.center.bmc.core.flow.cost.rule.impl.Unit;
import com.ai.runner.center.bmc.core.persistence.entity.BmcRecordFmt.BmcRecordFmtKey;
import com.ai.runner.center.bmc.core.util.BillingConstants;
import com.ai.runner.center.bmc.core.util.Records;

/**
 * 计费大师，总管计费功能
 * 
 * @author bixy
 * 
 */
public class BillingMaster {
	private static Logger logger = Logger.getLogger(BillingMaster.class);
	private Map<String, IBilling> rules = new HashMap<>();

	public BillingMaster(Map conf) {
		rules.put("step", new Step());
		rules.put("unit", new Unit(conf));
		rules.put("package", new Package(conf));
	}

	public List<ChargingDetailRecord> billing(Records records, BmcRecordFmtKey bmcRecordFmtKey, String systemId, String subsId, String psn, String sn) {
		// 取第一条数据的计算类型，因为默认情况下一个包的数据的cal_type都是一样的
		String calType = records.getString(0, BillingConstants.CAL_TYPE);
		List<ChargingDetailRecord> result = new ArrayList<>();
		IBilling billingRule = rules.get(calType);
		if (billingRule == null) {
			logger.error("couldn't billing for cal_type:" + calType);
			return result;
		}

		for (Map<String, String> map : records.getData()) {
			ChargingDetailRecord finalRecord = new ChargingDetailRecord();
			finalRecord.setBmcRecordFmtKey(bmcRecordFmtKey);
			finalRecord.put(BillingConstants.SYSTEM_ID, systemId);
			finalRecord.put(BillingConstants.SUBS_ID, subsId);
			finalRecord.put(BillingConstants.PSN, psn);
			finalRecord.put(BillingConstants.SN, sn);
			finalRecord.putAll(map);
			result.addAll(billingRule.billing(finalRecord));
		}
		return result;
	}
}
