package com.ai.runner.center.bmc.core.util;

import java.util.Map;

import com.ai.paas.ipaas.mcs.CacheFactory;
import com.ai.paas.ipaas.mcs.interfaces.ICacheClient;
import com.ai.paas.ipaas.uac.vo.AuthDescriptor;
import com.ai.runner.center.bmc.core.exception.BillingPrepareException;
import com.ai.runner.center.bmc.core.flow.cost.entity.PaasParam;
import com.google.gson.Gson;

public class DshmMcsClient {
	public static final String BILLING_DSHM_MCS_PARAM = "billing.dshm.mcs.param";

	private static ICacheClient icache;

	public static synchronized void createClient(Map conf) {
		PaasParam paasParam = (new Gson()).fromJson((String) conf.get(BILLING_DSHM_MCS_PARAM), PaasParam.class);
		AuthDescriptor ad = new AuthDescriptor(paasParam.getAuthAddr(), paasParam.getpId(), paasParam.getPassword(),
				paasParam.getSrvId());
		try {
			icache = CacheFactory.getClient(ad);
		} catch (Exception e) {
			throw new BillingPrepareException(e);
		}
	}

	public static ICacheClient getClient() {
		return icache;
	}
}
