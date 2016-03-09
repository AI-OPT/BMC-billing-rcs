package com.ai.runner.center.bmc.core.persistence.entity;

import java.io.Serializable;

public class CpDataQuality implements Serializable {

	private static final long serialVersionUID = 2852427006687284103L;
	private String systemId;
	private String tenantId;
	private String serviceId;
	private String dupKey;
	private String tbSuffixKey;
	private String script;
	
	public String getSystemId() {
		return systemId;
	}

	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}

	public String getDupKey() {
		return dupKey;
	}

	public void setDupKey(String dupKey) {
		this.dupKey = dupKey;
	}

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}
	
	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getTbSuffixKey() {
		return tbSuffixKey;
	}

	public void setTbSuffixKey(String tbSuffixKey) {
		this.tbSuffixKey = tbSuffixKey;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	
	
}
