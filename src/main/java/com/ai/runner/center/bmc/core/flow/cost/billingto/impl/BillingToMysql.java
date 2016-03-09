package com.ai.runner.center.bmc.core.flow.cost.billingto.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.ai.runner.center.bmc.core.entity.JdbcParam;
import com.ai.runner.center.bmc.core.exception.BillingPrepareException;
import com.ai.runner.center.bmc.core.flow.cost.billingto.BillingToException;
import com.ai.runner.center.bmc.core.flow.cost.billingto.IBillingTo;
import com.ai.runner.center.bmc.core.flow.cost.entity.AccuFieldValue;
import com.ai.runner.center.bmc.core.flow.cost.entity.GroupFieldValue;
import com.ai.runner.center.bmc.core.util.JdbcTemplate;

/**
 * 累帐的最终目标为mysql数据库
 * 
 * @author bixy
 * 
 */
public class BillingToMysql implements IBillingTo {
	private static Logger logger = Logger.getLogger(BillingToMysql.class);
	private Map<GroupFieldValue, AccuFieldValue> finished;
	private Connection connection;
	private JdbcParam jdbcParam;

	public BillingToMysql(JdbcParam jdbcParam) {
		this.jdbcParam = jdbcParam;
		try {
			connection = JdbcTemplate.getConnection(jdbcParam);
		} catch (ClassNotFoundException e) {
			throw new BillingPrepareException(e);
		} catch (SQLException e) {
			throw new BillingPrepareException(e);
		}
	}

	@Override
	public void sumBill(Map<GroupFieldValue, AccuFieldValue> data) throws BillingToException {
		logger.info("sum billing data size ============" + data.size());
		try {
			finished = new HashMap<GroupFieldValue, AccuFieldValue>();
			connection = JdbcTemplate.refreshConnection(connection, jdbcParam);
			Statement statement = connection.createStatement();
			for (Entry<GroupFieldValue, AccuFieldValue> entry : data.entrySet()) {
				GroupFieldValue groupFieldValue = entry.getKey();
				AccuFieldValue accuFieldValue = entry.getValue();
				String updateSql = groupFieldValue.getAccuRule().getUpdateSql(groupFieldValue, accuFieldValue);
				logger.info("to mysql sql---------" + updateSql);
				int result = statement.executeUpdate(updateSql);
				// 目标数据库不存在被累计的记录，则新增一条
				if (result == 0) {
					String insertSql = groupFieldValue.getAccuRule().getInsertSql(groupFieldValue, accuFieldValue);
					logger.info("to mysql sql---------" + insertSql);
					statement.execute(insertSql);
				}
				finished.put(groupFieldValue, accuFieldValue);
			}
			statement.close();
			connection.commit();
		} catch (ClassNotFoundException e) {
			logger.error(e);
		} catch (SQLException e) {
			throw new BillingToException(e);
			//这里要加回收机制，咱没有支持
		}
	}

	public Map<GroupFieldValue, AccuFieldValue> getFinished() {
		return finished;
	}

}
