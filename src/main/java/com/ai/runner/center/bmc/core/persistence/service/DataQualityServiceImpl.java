package com.ai.runner.center.bmc.core.persistence.service;

import java.util.List;

import com.ai.runner.center.bmc.core.persistence.dao.BmcDataQualityDao;
import com.ai.runner.center.bmc.core.persistence.entity.CpDataQuality;

public class DataQualityServiceImpl implements DataQualityService {

	private BmcDataQualityDao dataQualityDao;
	
	@Override
	public List<CpDataQuality> queryAllData() {
		return dataQualityDao.queryAllData();
	}
	
	public void setDataQualityDao(BmcDataQualityDao dataQualityDao) {
		this.dataQualityDao = dataQualityDao;
	}

}
