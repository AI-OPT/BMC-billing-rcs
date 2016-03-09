package com.ai.runner.center.bmc.core.entity;

import java.util.Map;

public class JdbcParam {
	public static final String JDBC_DRIVER = "jdbc.driver";
	public static final String JDBC_URL = "jdbc.url";
	public static final String JDBC_USERNAME = "jdbc.username";
	public static final String JDBC_PASSWORD = "jdbc.password";

	private String driver;
	private String url;
	private String username;
	private String password;

	public static JdbcParam getInstance(Map<String, String> conf) {
		return new JdbcParam(conf.get(JDBC_DRIVER), conf.get(JDBC_URL), conf.get(JDBC_USERNAME), conf.get(JDBC_PASSWORD));
	}

	public JdbcParam(String driver, String url, String username, String password) {
		this.driver = driver;
		this.url = url;
		this.username = username;
		this.password = password;
	}

	public String getDriver() {
		return driver;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
