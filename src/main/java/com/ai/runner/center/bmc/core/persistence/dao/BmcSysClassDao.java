package com.ai.runner.center.bmc.core.persistence.dao;

import java.util.List;

import com.ai.runner.center.bmc.core.persistence.entity.BmcSysClass;

public interface BmcSysClassDao {

	String name = "BmcSysClassDao";
	
	List<BmcSysClass> queryAllData();
	
}
