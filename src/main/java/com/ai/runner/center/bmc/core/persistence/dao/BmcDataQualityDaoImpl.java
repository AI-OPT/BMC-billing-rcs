package com.ai.runner.center.bmc.core.persistence.dao;

import java.util.List;

import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.ai.runner.center.bmc.core.persistence.entity.CpDataQuality;
import com.ai.runner.center.bmc.core.util.JdbcTemplate;

public class BmcDataQualityDaoImpl implements BmcDataQualityDao {
	
	@Override
	public List<CpDataQuality> queryAllData() {
		
		StringBuilder sql = new StringBuilder();
		sql.append("select t.system_id systemId,t.tenant_id tenantId,t.service_id serviceId,");
		sql.append("t.dup_key dupKey,t.tb_suffix_key tbSuffixKey,t.script script ");
		sql.append("from bmc_dataquality t");

		return JdbcTemplate.query(sql.toString(), new BeanListHandler<CpDataQuality>(CpDataQuality.class));
	}

	
	
}
