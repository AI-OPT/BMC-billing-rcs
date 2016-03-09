package com.ai.runner.center.bmc.core.util;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;

import com.ai.runner.center.bmc.core.entity.JdbcParam;

public class JdbcTemplate {
	public static final String JDBC_DRIVER = "jdbc.driver";
	public static final String JDBC_URL = "jdbc.url";
	public static final String JDBC_USERNAME = "jdbc.username";
	public static final String JDBC_PASSWORD = "jdbc.password";

	private static Logger logger = Logger.getLogger(JdbcTemplate.class);

	/**
	 * 根据参数获取一个数据库连接
	 * 
	 * @param jdbcParam
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static Connection getConnection(JdbcParam jdbcParam) throws ClassNotFoundException, SQLException {
		Class.forName(jdbcParam.getDriver());
		Connection conn = DriverManager.getConnection(jdbcParam.getUrl(), jdbcParam.getUsername(), jdbcParam.getPassword());
		conn.setAutoCommit(false);
		return conn;
	}

	/**
	 * 给定一个数据库连接，如果失效，则返回一个新的连接
	 * 
	 * @param conn
	 * @param jdbcParam
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static Connection refreshConnection(Connection conn, JdbcParam jdbcParam) throws ClassNotFoundException, SQLException {
		// 60秒没反应，需要重连
		if (conn.isValid(60)) {
			return conn;
		} else
			return getConnection(jdbcParam);
	}

	/**
	 * 查询后关闭连接的查询
	 * 
	 * @param sql
	 * @param conn
	 * @param rsh
	 * @return
	 */
	public static <T> List<T> closeAfterQuery(String sql, Connection conn, ResultSetHandler<List<T>> rsh) {
		List<T> list = new ArrayList<>();
		try {
			QueryRunner runner = new QueryRunner();
			list = runner.query(conn, sql, rsh);
			conn.commit();
		} catch (SQLException e) {
			logger.error(e);
		} finally {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				logger.error(e1);
			}
			DbUtils.closeQuietly(conn);
		}
		return list;
	}

	public static <T> List<T> query(String sql, Connection conn, ResultSetHandler<List<T>> rsh, Object... params) {
		List<T> list = new ArrayList<>();
		try {
			QueryRunner runner = new QueryRunner();
			list = runner.query(conn, sql, rsh, params);
			conn.commit();
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				logger.error(e1);
			}
			logger.error(e);
		} 
		return list;
	}

	/**
	 * 查询
	 * 
	 * @param sql
	 * @param conn
	 * @param rsh
	 * @return
	 */
	public static <T> List<T> query(String sql, Connection conn, ResultSetHandler<List<T>> rsh) {
		List<T> list = new ArrayList<>();
		try {
			QueryRunner runner = new QueryRunner();
			list = runner.query(conn, sql, rsh);
			conn.commit();
		} catch (SQLException e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				logger.error(e1);
			}
			logger.error(e);
		}
		return list;
	}

	public static <T> List<T> query(String sql, ResultSetHandler<List<T>> rsh) {
		List<T> result = null;
		Connection conn = null;
		try {
			conn = JdbcProxy.getConnection();
			result = query(sql,conn,rsh);
			conn.commit();
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				logger.error(e1);
			}
			logger.error(e);
		}
		return result;
	}

	/**
	 * 更新（包括UPDATE、INSERT、DELETE，返回受影响的行数）
	 * 
	 * @param DataSourceName
	 * @param sql
	 * @param params
	 * @return
	 */
	public static int update(String sql, Object... params) {
		int result = 0;
		Connection conn = null;
		QueryRunner runner = new QueryRunner();
		try {
			conn = JdbcProxy.getConnection();
			result = runner.update(conn, sql, params);
			conn.commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			try {
				conn.rollback();
			} catch (SQLException e1) {
				logger.error(e1.getMessage());
			}
		}
		return result;
	}

	public static int update(String sql) {
		int result = 0;
		Connection conn = null;
		QueryRunner runner = new QueryRunner();
		try {
			conn = JdbcProxy.getConnection();
			result = runner.update(conn, sql);
			conn.commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			try {
				conn.rollback();
			} catch (SQLException e1) {
				logger.error(e1.getMessage());
			}
		}
		return result;
	}

	public static int[] batchUpdate(String sql, List<Object[]> inputParams) {
		int[] results = null;
		Connection conn = null;
		QueryRunner runner = new QueryRunner();
		int len = inputParams.size();
		Object[][] params = new Object[len][];
		try {
			for (int i = 0; i < len; i++) {
				params[i] = inputParams.get(i);
			}
			conn = JdbcProxy.getConnection();
			results = runner.batch(conn, sql, params);
			conn.commit();
		} catch (Exception e) {
			logger.error(e.getMessage());
			try {
				conn.rollback();
			} catch (SQLException e1) {
				logger.error(e1.getMessage());
			}
		}
		return results;
	}

}
