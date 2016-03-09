package com.ai.runner.center.bmc.core.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.runner.center.bmc.core.entity.JdbcParam;
import com.ai.runner.center.bmc.core.exception.BillingPrepareException;
import com.ai.runner.center.bmc.core.persistence.dao.BmcOutputDao;
import com.ai.runner.center.bmc.core.persistence.entity.BmcOutputDetail;
import com.ai.runner.center.bmc.core.persistence.entity.BmcOutputInfo;
import com.ai.runner.center.bmc.core.persistence.entity.BmcRecordFmt.BmcRecordFmtKey;

public class HbaseOutputInfos {

//	PUBLIC ENUM LABEL {
//		// 主表标签
//		BUSINESS_ID, BUSINESS_TYPE, TABLE_PREFIX, TABLE_POSTFIX, OUTPUT_TYPE, KEY_SEQ,
//		// 子表标签
//		COLUMN_NAME, PARAM_NAME, LEVEL, ORDER
//	}
//
//	PUBLIC STATIC FINAL STRING KEY_INFO = "OUTPUT_INFO";
//	PUBLIC STATIC FINAL STRING KEY_DETAIL = "OUTPUT_DETAIL";

	private static Logger logger = LoggerFactory.getLogger(HbaseOutputInfos.class);
	private Map<BmcRecordFmtKey, List<BmcOutputInfo>> outputMapping = new HashMap<>();
	private JdbcParam jdbcParam;

	public HbaseOutputInfos(JdbcParam jdbcParam) {
		this.jdbcParam = jdbcParam;
		loadData();
	}

	private void loadData() {
		Connection conn = null;
		try {
			conn = JdbcTemplate.getConnection(jdbcParam);
		} catch (ClassNotFoundException e) {
			throw new BillingPrepareException(e);
		} catch (SQLException e) {
			throw new BillingPrepareException(e);
		}
		BmcOutputDao outputDao = (BmcOutputDao) DaoFactory.getInstance(BmcOutputDao.name);
		List<BmcOutputInfo> infoObjs = outputDao.queryAllOutputData(conn);
		List<BmcOutputDetail> detailObjs = null;
		for (BmcOutputInfo infoObj : infoObjs) {
			logger.debug("------[OutputConfigUtils.loadData=]" + infoObjs.toString());
			detailObjs = outputDao.queryOutputDetailByInfoCode(conn, String.valueOf(infoObj.getInfoCode()));
			if (detailObjs != null && detailObjs.size() != 0) {
				infoObj.setDetails(detailObjs);
			}
			infoObj.createTableName();
			BmcRecordFmtKey key = new BmcRecordFmtKey(infoObj.getTenantId(), infoObj.getServiceId());
			List<BmcOutputInfo> outputInfos = outputMapping.get(key);
			if (outputInfos == null) {
				outputInfos = new ArrayList<>();
			}
			outputInfos.add(infoObj);
			outputMapping.put(key, outputInfos);
		}
	}

	public List<BmcOutputInfo> getOutputMappingValue(BmcRecordFmtKey key) {
		return outputMapping.get(key);
	}

}
