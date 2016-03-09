package com.ai.runner.center.bmc.core.persistence.dao;

import java.util.List;

import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.ai.runner.center.bmc.core.persistence.entity.BmcSysClass;
import com.ai.runner.center.bmc.core.util.JdbcTemplate;

public class BmcSysClassDaoImpl implements BmcSysClassDao {
	
	@Override
	public List<BmcSysClass> queryAllData() {
		StringBuilder strSql = new StringBuilder();
		strSql.append("select t.sys_key sysKey,t.sys_class sysClass ");
		strSql.append("from bmc_sys_class t");
		return JdbcTemplate.query(strSql.toString(), new BeanListHandler<BmcSysClass>(BmcSysClass.class));
	}
	
}
