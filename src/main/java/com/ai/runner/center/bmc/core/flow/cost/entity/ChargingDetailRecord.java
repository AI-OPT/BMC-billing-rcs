package com.ai.runner.center.bmc.core.flow.cost.entity;

import java.util.HashMap;
import java.util.Map;

import com.ai.runner.center.bmc.core.persistence.entity.BmcRecordFmt.BmcRecordFmtKey;
import com.ai.runner.center.bmc.core.util.BillingConstants;

/**
 * 入库详单对象
 * 
 * @author bixy
 * 
 */
public class ChargingDetailRecord {
	private String accountPeriod;
	private BmcRecordFmtKey bmcRecordFmtKey;
	private Map<String, String> fields = new HashMap<String, String>();
	private Map<Integer, SubjectAndPriceValue> fees;

	public Map<String, String> getFields() {
		return fields;
	}

	public void putAll(Map<String, String> m) {
		fields.putAll(m);
	}

	public void put(String key, String value) {
		fields.put(key, value);
	}

	public void remove(String key) {
		fields.remove(key);
	}

	public BmcRecordFmtKey getBmcRecordFmtKey() {
		return bmcRecordFmtKey;
	}

	public String get(String key) {
		return fields.get(key);
	}

	public String getAccountPeriod() {
		return accountPeriod;
	}

	public void setAccountPeriod(String accountPeriod) {
		this.accountPeriod = accountPeriod;
	}

	public Map<Integer, SubjectAndPriceValue> getFees() {
		return fees;
	}

	public void setFees(Map<Integer, SubjectAndPriceValue> fees) {
		this.fees = fees;
	}

	public void setBmcRecordFmtKey(BmcRecordFmtKey bmcRecordFmtKey) {
		this.bmcRecordFmtKey = bmcRecordFmtKey;
		fields.put(BillingConstants.TENANT_ID, bmcRecordFmtKey.getTenantId());
		fields.put(BillingConstants.SERVICE_ID, bmcRecordFmtKey.getBusiType());
	}

}
