package com.ai.runner.center.bmc.core.util;

import java.util.HashMap;
import java.util.Map;

import com.ai.runner.center.bmc.core.persistence.dao.BmcDataQualityDao;
import com.ai.runner.center.bmc.core.persistence.dao.BmcDataQualityDaoImpl;
import com.ai.runner.center.bmc.core.persistence.dao.BmcFailureBillDao;
import com.ai.runner.center.bmc.core.persistence.dao.BmcFailureBillDaoImpl;
import com.ai.runner.center.bmc.core.persistence.dao.BmcOutputDao;
import com.ai.runner.center.bmc.core.persistence.dao.BmcOutputDaoImpl;
import com.ai.runner.center.bmc.core.persistence.dao.BmcSysClassDao;
import com.ai.runner.center.bmc.core.persistence.dao.BmcSysClassDaoImpl;

public class DaoFactory {

	private static Map<String, Object> daoMap = new HashMap<String, Object>();
	
	static{
		daoMap.put(BmcDataQualityDao.name, new BmcDataQualityDaoImpl());
		daoMap.put(BmcFailureBillDao.name, new BmcFailureBillDaoImpl());
		daoMap.put(BmcSysClassDao.name, new BmcSysClassDaoImpl());
		daoMap.put(BmcOutputDao.name, new BmcOutputDaoImpl());
	}
	
	public static Object getInstance(String daoName){
		return daoMap.get(daoName);
	}
}
