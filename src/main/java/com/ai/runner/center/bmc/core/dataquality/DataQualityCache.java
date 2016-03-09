package com.ai.runner.center.bmc.core.dataquality;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.runner.center.bmc.core.persistence.dao.BmcDataQualityDao;
import com.ai.runner.center.bmc.core.persistence.entity.CpDataQuality;
import com.ai.runner.center.bmc.core.util.DaoFactory;

public class DataQualityCache {

	// private static Logger logger =
	// LoggerFactory.getLogger(DataQualityManager.class);
	private static MultiValueMap dupKeyMap = new MultiValueMap();
	private static Map<String, String> suffixKeyMap = new HashMap<String, String>();
	// private static Map<String,String> scriptMap = new
	// HashMap<String,String>();
	private static DataQualityCache instance = null;

	public static DataQualityCache getInstance() {
		if (instance == null) {
			synchronized (DataQualityCache.class) {
				if (instance == null) {
					instance = new DataQualityCache();
					loadData();
				}
			}
		}
		return instance;
	}

	private static void loadData() {
		//DataQualityService dataQualityService = (DataQualityService) ApplicationContextUtil.getBean("dataQualityService");
		BmcDataQualityDao dataQualityDao = (BmcDataQualityDao)DaoFactory.getInstance(BmcDataQualityDao.name);
		List<CpDataQuality> dataQualitys = dataQualityDao.queryAllData();
		String[] dupKeys = null;
		for (CpDataQuality dataQuality : dataQualitys) {
			StringBuilder key = new StringBuilder();
			key.append(dataQuality.getSystemId());
			key.append(dataQuality.getTenantId());
			key.append(dataQuality.getServiceId());
			dupKeys = StringUtils.splitPreserveAllTokens(dataQuality.getDupKey(), ",");
			for (String dupKey : dupKeys) {
				dupKeyMap.put(key.toString(), dupKey);
			}
			suffixKeyMap.put(key.toString(), dataQuality.getTbSuffixKey());
			ScriptManager.getInstance().addScriptProcessor(key.toString(), StringUtils.defaultString(dataQuality.getScript()));
		}
	}

	/**
	 * 
	 * @param key
	 *            : businessId+service_id
	 * @return
	 */
	public List<String> getDupKeyNames(String key) {
		return (List<String>) dupKeyMap.get(key);
	}

	public String getTbSuffixKey(String key) {
		return suffixKeyMap.get(key);
	}

}
