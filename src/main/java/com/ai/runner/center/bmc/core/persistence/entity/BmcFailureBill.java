package com.ai.runner.center.bmc.core.persistence.entity;

import java.io.Serializable;
import java.sql.Timestamp;

public class BmcFailureBill implements Serializable {

	private static final long serialVersionUID = -865272664278537978L;
	private String systemId;
	private String serviceId;
	private String tenantId;
	private String psn;
	private String source;
	private int row_num;
	private Timestamp packetCreateDate;
	private String failStep;
	private String failCode;
	private String failReason;
	private String failPakcet;
	private Timestamp failDate;
	private String sn;

	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
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

	public String getPsn() {
		return psn;
	}

	public void setPsn(String psn) {
		this.psn = psn;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public int getRow_num() {
		return row_num;
	}

	public void setRow_num(int row_num) {
		this.row_num = row_num;
	}

	public Timestamp getPacketCreateDate() {
		return packetCreateDate;
	}

	public void setPacketCreateDate(Timestamp packetCreateDate) {
		this.packetCreateDate = packetCreateDate;
	}

	public String getFailStep() {
		return failStep;
	}

	public void setFailStep(String failStep) {
		this.failStep = failStep;
	}

	public String getFailReason() {
		return failReason;
	}

	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}

	public String getFailPakcet() {
		return failPakcet;
	}

	public void setFailPakcet(String failPakcet) {
		this.failPakcet = failPakcet;
	}

	public Timestamp getFailDate() {
		return failDate;
	}

	public void setFailDate(Timestamp failDate) {
		this.failDate = failDate;
	}

	public String getFailCode() {
		return failCode;
	}

	public void setFailCode(String failCode) {
		this.failCode = failCode;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}
	
}
