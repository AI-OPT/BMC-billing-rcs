package com.ai.runner.center.bmc.core.persistence.dao;

import java.sql.Connection;
import java.util.List;

import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.lang.StringUtils;

import com.ai.runner.center.bmc.core.persistence.entity.BmcOutputDetail;
import com.ai.runner.center.bmc.core.persistence.entity.BmcOutputInfo;
import com.ai.runner.center.bmc.core.util.JdbcTemplate;

public class BmcOutputDaoImpl implements BmcOutputDao {

	@Override
	public List<BmcOutputInfo> queryAllOutputData(Connection conn) {
		return JdbcTemplate.query(
				"select a.info_code infoCode,a.tenant_id tenantId,a.service_id serviceId,a.table_prefix tablePrefix,a.output_type outputType,a.output_name outputName,a.key_seq keySeq,a.seq_name seqName from bmc_output_info a",
				conn, new BeanListHandler<BmcOutputInfo>(BmcOutputInfo.class));
	}

	@Override
	public List<BmcOutputDetail> queryOutputDetailByInfoCode(Connection conn, String infoCode) {
		StringBuilder sql = new StringBuilder("select a.detail_code detailCode,a.info_code infoCode,a.column_name columnName,a.param_name paramName,a.is_key isKey,a.display_order displayOrder from bmc_output_detail a");
		if (StringUtils.isNotBlank(infoCode)) {
			sql.append(" where a.info_code=").append(infoCode);
		}
		sql.append(" order by a.display_order ");
		return JdbcTemplate.query(sql.toString(), conn, new BeanListHandler<BmcOutputDetail>(BmcOutputDetail.class));
	}

}
