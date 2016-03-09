package com.ai.runner.center.bmc.core.biz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ai.runner.center.bmc.core.biz.api.ISysProcessor;
import com.ai.runner.center.bmc.core.persistence.dao.BmcSysClassDao;
import com.ai.runner.center.bmc.core.persistence.entity.BmcSysClass;
import com.ai.runner.center.bmc.core.util.BmcException;
import com.ai.runner.center.bmc.core.util.DaoFactory;
import com.ai.runner.center.bmc.core.util.ReflectionUtils;

public class BizFactory {

	private Map<String, String> bizProMap = new HashMap<String, String>();
	private BmcSysClassDao bmcSysClassDao;

	public BizFactory() {
		// bizProMap.put("MVNE", "");
		// bmcSysClassDao =
		// (BmcSysClassDao)ApplicationContextUtil.getBean("bmcSysClassDao");
		bmcSysClassDao = (BmcSysClassDao) DaoFactory.getInstance(BmcSysClassDao.name);
		List<BmcSysClass> sysClazzs = bmcSysClassDao.queryAllData();
		for (BmcSysClass bmcSysClass : sysClazzs) {
			bizProMap.put(StringUtils.trim(bmcSysClass.getSysKey()), StringUtils.trim(bmcSysClass.getSysClass()));
		}
	}

	public ISysProcessor getProcessorByKey(String business_id, String tenant_id) throws BmcException {
		StringBuilder key = new StringBuilder();
		key.append(business_id).append("$").append(tenant_id);
		String processorClazz = bizProMap.get(key.toString());
		if (StringUtils.isBlank(processorClazz)) {
			key = new StringBuilder();
			key.append(business_id).append("*");
			processorClazz = bizProMap.get(key.toString());
			if (StringUtils.isBlank(processorClazz)) {
				throw new BmcException("", "bmc_sys_class没有配置此业务的通用class信息");
			}
		}
		return ReflectionUtils.getSysProcessorObj(processorClazz);
	}

	public ISysProcessor getProcessor(String tenant_id, String service_id) throws BmcException {
		StringBuilder key = new StringBuilder();
		key.append(tenant_id).append("$").append(service_id);
		String processorClazz = bizProMap.get(key.toString());
		if (StringUtils.isBlank(processorClazz)) {
			throw new BmcException("", "bmc_sys_class没有配置此业务的通用class信息");
		}
		return ReflectionUtils.getSysProcessorObj(processorClazz);
	}
}
