package com.ai.runner.center.bmc.core.persistence.dao;

import java.util.List;

import com.ai.runner.center.bmc.core.persistence.entity.CpDataQuality;

public interface BmcDataQualityDao {
	
	String name = "BmcDataQualityDao";

	List<CpDataQuality> queryAllData();
	
	
}
