package com.ai.runner.center.bmc.core.persistence.entity;

import java.io.Serializable;

public class BmcSysClass implements Serializable {
	
	private static final long serialVersionUID = -1146353149543417234L;
	private String sysKey;
	private String sysClass;

	public String getSysKey() {
		return sysKey;
	}

	public void setSysKey(String sysKey) {
		this.sysKey = sysKey;
	}

	public String getSysClass() {
		return sysClass;
	}

	public void setSysClass(String sysClass) {
		this.sysClass = sysClass;
	}

}
