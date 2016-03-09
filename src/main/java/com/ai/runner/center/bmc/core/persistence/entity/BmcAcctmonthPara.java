package com.ai.runner.center.bmc.core.persistence.entity;

public class BmcAcctmonthPara {
	public enum StatusType {
		current, passed, future
	};

	private String tenantId;
	private String serviceId;
	private String startMonth;
	private String endMonth;
	private Integer timeDelay;
	private String status;

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public Integer getTimeDelay() {
		return timeDelay;
	}

	public void setTimeDelay(Integer timeDelay) {
		this.timeDelay = timeDelay;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getStartMonth() {
		return startMonth;
	}

	public void setStartMonth(String startMonth) {
		this.startMonth = startMonth;
	}

	public String getEndMonth() {
		return endMonth;
	}

	public void setEndMonth(String endMonth) {
		this.endMonth = endMonth;
	}

}
