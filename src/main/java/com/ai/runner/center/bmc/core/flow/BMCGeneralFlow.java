package com.ai.runner.center.bmc.core.flow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.ai.paas.ipaas.rcs.common.FlowConfig;
import com.ai.paas.ipaas.rcs.common.IFlowDefine;
import com.ai.paas.ipaas.rcs.common.Module;
import com.ai.paas.ipaas.rcs.param.FlowParam;
import com.ai.runner.center.bmc.core.flow.cost.CostCalculateHandler;
import com.ai.runner.center.bmc.core.flow.fill.FillDataHandler;
import com.ai.runner.center.bmc.core.flow.format.DataQualityHandler;
import com.ai.runner.center.bmc.core.util.BillingConstants;
import com.ai.runner.center.bmc.core.util.RecordUtil;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import backtype.storm.Config;

/**
 * BMC通用拓扑图
 * 
 * @author majun
 *
 */
public class BMCGeneralFlow implements IFlowDefine {
	private static Logger logger = Logger.getLogger(BMCGeneralFlow.class);
	public static final String BILLING_WORKER_NUM = "billing.worker.num";
	public static final String BILLING_UNPACK_NUM = "billing.unpack.num";
	public static final String BILLING_DATA_QUALITY_NUM = "billing.data.quality.num";
	public static final String BILLING_RULE_ADAPTOR_NUM = "billing.rule.adaptor.num";
	public static final String BILLING_COST_CALCULATOR_NUM = "billing.cost.calculator.num";

	/**
	 * 定义流程的默认配置参数
	 */
	@Override
	public void configure(String[] args, FlowConfig aConfig) {
		aConfig.setDebug(false);
		Map<String, String> configs = getConfig(args);
		String pending = configs.get(Config.TOPOLOGY_MAX_SPOUT_PENDING);
		logger.info("get pending..............." + pending);
		pending = (pending == null) ? "1000" : pending;
		aConfig.setMaxInputPending(Integer.parseInt(pending));
		aConfig.setConf(Config.TOPOLOGY_ACKER_EXECUTORS, configs.get(Config.TOPOLOGY_ACKER_EXECUTORS));
		for (Entry<String, String> entry : configs.entrySet())
			aConfig.setConf(entry.getKey(), entry.getValue().toString());
	}

	private Map<String, String> getConfig(String[] args) {
		Map<String, String> result = new HashMap<>();
		if (args.length > 0) {
			// 只解析args【0】
			Gson gson = new Gson();
			JsonObject jo = gson.fromJson(args[0], JsonObject.class);
			for (Entry<String, JsonElement> entry : jo.entrySet()) {
				JsonElement je = entry.getValue();
				if (je.isJsonObject())
					result.put(entry.getKey(), entry.getValue().toString());
				else

					result.put(entry.getKey(), entry.getValue().getAsString());
			}
		}
		return result;
	}

	/**
	 * 定义拓扑的主流程
	 */
	@Override
	public void define(String[] args, Module aModule, FlowParam aParams) {
		Map<String, String> config = getConfig(args);
		aParams.setNumWorkers(getNum(config, BILLING_WORKER_NUM, 2));
		aModule.setMdsInput(config);
		aModule.setProcessor("unpacking", UnpackingHandler.class, getNum(config, BILLING_UNPACK_NUM, 1), "shuffle",
				"mds-input");
		aModule.setProcessor("data-quality", DataQualityHandler.class, getNum(config, BILLING_DATA_QUALITY_NUM, 1),
				null, "fields", new ArrayList<String>(Arrays.asList(RecordUtil.SERIAL_NUMBER)), "unpacking");
		aModule.setProcessor("rule-adaptor", FillDataHandler.class, getNum(config, BILLING_RULE_ADAPTOR_NUM, 1),
				"shuffle", "data-quality");
		aModule.setProcessor("cost-calculator", CostCalculateHandler.class,
				getNum(config, BILLING_COST_CALCULATOR_NUM, 1), null, "fields",
				new ArrayList<String>(Arrays.asList(BillingConstants.SUBS_ID)), "rule-adaptor");
	}

	private int getNum(Map<String, String> config, String param, int defaultNum) {
		String str = config.get(param);
		if (StringUtils.isEmpty(str)) {
			logger.warn(param + " is not set , use default " + defaultNum);
			return defaultNum;
		} else {
			return Integer.parseInt(str);
		}
	}
}
