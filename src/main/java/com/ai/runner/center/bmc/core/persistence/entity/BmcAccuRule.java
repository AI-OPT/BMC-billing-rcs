package com.ai.runner.center.bmc.core.persistence.entity;

public class BmcAccuRule {
	private String tenantId;
	private String serviceId;
	private String statId;
	private String tableName;
	private String groupFields;
	private String accuFields;

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getStatId() {
		return statId;
	}

	public void setStatId(String statId) {
		this.statId = statId;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getGroupFields() {
		return groupFields;
	}

	public void setGroupFields(String groupFields) {
		this.groupFields = groupFields;
	}

	public String getAccuFields() {
		return accuFields;
	}

	public void setAccuFields(String accuFields) {
		this.accuFields = accuFields;
	}
}
