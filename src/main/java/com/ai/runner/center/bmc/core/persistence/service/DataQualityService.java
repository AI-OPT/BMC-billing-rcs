package com.ai.runner.center.bmc.core.persistence.service;

import java.util.List;

import com.ai.runner.center.bmc.core.persistence.entity.CpDataQuality;

public interface DataQualityService {
	
	List<CpDataQuality> queryAllData();
	
}
