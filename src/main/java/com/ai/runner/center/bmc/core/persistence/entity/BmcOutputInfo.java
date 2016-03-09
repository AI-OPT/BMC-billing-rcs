package com.ai.runner.center.bmc.core.persistence.entity;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import com.ai.runner.center.bmc.core.flow.cost.entity.ChargingDetailRecord;
import com.ai.runner.center.bmc.core.util.BillingConstants;

public class BmcOutputInfo implements Serializable {

	private static final long serialVersionUID = 7096287403824940589L;
	private Long infoCode;
	private String serviceId;
	private String tenantId;
	private String tablePrefix;
	private String outputType;
	private String outputName;
	private String keySeq;
	private String seqName;

	private String tableName;
	private Collection<BmcOutputDetail> rowKeys;
	private List<BmcOutputDetail> details;

	public void createTableName() {
		StringBuilder tableNameBuilder = new StringBuilder();
		tableNameBuilder.append(serviceId).append("_");
		tableNameBuilder.append(tenantId).append("_");
		tableNameBuilder.append(tablePrefix).append("_");
		this.tableName = tableNameBuilder.toString();
	}

	public String getRowKey(ChargingDetailRecord chargingDetailRecord) {
		StringBuilder stringBuilder = new StringBuilder();
		for (BmcOutputDetail cpOutputDetail : rowKeys) {
			stringBuilder.append(chargingDetailRecord.get(cpOutputDetail.getParamName()));
			stringBuilder.append(BillingConstants.FIELD_SPLIT);
		}
		stringBuilder.deleteCharAt(stringBuilder.length() - 1);
		return stringBuilder.toString();
	}

	public Long getInfoCode() {
		return infoCode;
	}

	public void setInfoCode(Long infoCode) {
		this.infoCode = infoCode;
	}

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

	public String getTablePrefix() {
		return tablePrefix;
	}

	public void setTablePrefix(String tablePrefix) {
		this.tablePrefix = tablePrefix;
	}

	public String getOutputType() {
		return outputType;
	}

	public void setOutputType(String outputType) {
		this.outputType = outputType;
	}

	public String getKeySeq() {
		return keySeq;
	}

	public void setKeySeq(String keySeq) {
		this.keySeq = keySeq;
	}

	public String getOutputName() {
		return outputName;
	}

	public void setOutputName(String outputName) {
		this.outputName = outputName;
	}

	public String getSeqName() {
		return seqName;
	}

	public void setSeqName(String seqName) {
		this.seqName = seqName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<BmcOutputDetail> getDetails() {
		return details;
	}

	public void setDetails(List<BmcOutputDetail> details) {
		TreeMap<Integer, BmcOutputDetail> keys = new TreeMap<>();
		for (BmcOutputDetail cpOutputDetail : details) {
			if ("Y".equals(cpOutputDetail.getIsKey())) {
				keys.put(cpOutputDetail.getDisplayOrder(), cpOutputDetail);
			}
		}
		this.rowKeys = keys.values();
		this.details = details;
		this.details.removeAll(rowKeys);
	}

}