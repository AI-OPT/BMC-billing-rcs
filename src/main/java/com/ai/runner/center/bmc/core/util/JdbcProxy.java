package com.ai.runner.center.bmc.core.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.runner.center.bmc.core.entity.JdbcParam;

/**
 * jdbc代理类
 * @author majun
 *
 */
public class JdbcProxy {

	private static Logger logger = LoggerFactory.getLogger(JdbcProxy.class);
	private static JdbcParam jdbcParam;
	private static Connection conn;
	
	public static void loadResource(Map<String,String> config){
		jdbcParam = JdbcParam.getInstance(config);
		try {
			conn = JdbcTemplate.getConnection(jdbcParam);
		} catch (Exception e) {
			logger.error("", e);
		}
	}

	public static Connection getConnection() throws Exception{
		return JdbcTemplate.refreshConnection(conn, jdbcParam);
	}
	
}
