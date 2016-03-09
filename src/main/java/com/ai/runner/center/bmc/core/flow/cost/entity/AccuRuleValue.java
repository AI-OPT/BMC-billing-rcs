package com.ai.runner.center.bmc.core.flow.cost.entity;

public class AccuRuleValue {
	private String tableName;
	private ChargingDetailRecord groupFieldValue;
	private AccuFieldValue accuFieldValue;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public ChargingDetailRecord getGroupFieldValue() {
		return groupFieldValue;
	}

	public void setGroupFieldValue(ChargingDetailRecord groupFieldValue) {
		this.groupFieldValue = groupFieldValue;
	}

	public AccuFieldValue getAccuFieldValue() {
		return accuFieldValue;
	}

	public void setAccuFieldValue(AccuFieldValue accuFieldValue) {
		this.accuFieldValue = accuFieldValue;
	}

}
