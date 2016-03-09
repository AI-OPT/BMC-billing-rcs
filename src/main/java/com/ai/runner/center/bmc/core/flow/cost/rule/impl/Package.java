package com.ai.runner.center.bmc.core.flow.cost.rule.impl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.ai.runner.center.bmc.core.flow.cost.CostFactory;
import com.ai.runner.center.bmc.core.flow.cost.credit.ICreditControl;
import com.ai.runner.center.bmc.core.flow.cost.entity.ChargingDetailRecord;
import com.ai.runner.center.bmc.core.flow.cost.entity.FeeTypeAndSubject;
import com.ai.runner.center.bmc.core.flow.cost.resourcebook.IAccessResourceBook;
import com.ai.runner.center.bmc.core.flow.cost.rule.AbstractBilling;
import com.ai.runner.center.bmc.core.flow.cost.rule.IBilling;
import com.ai.runner.center.bmc.core.flow.cost.rule.IRateFinder;
import com.ai.runner.center.bmc.core.util.BillingConstants;
import com.google.gson.Gson;

/**
 * 套餐包模式计费
 * 
 * @author bixy
 * 
 */
public class Package extends AbstractBilling implements IBilling {
	private static Logger logger = Logger.getLogger(Package.class);
	private Gson jsonConverter = new Gson();
	private IAccessResourceBook accessResourceBook;
	private ICreditControl creditControl;

	public Package(Map conf) {
		logger.debug("set accessResourceBook " + (String) conf.get(IAccessResourceBook.ACCESS_RESOURCEBOOK));
		logger.debug("set creditControl " + (String) conf.get(ICreditControl.CREDIT_CONTROL));
		accessResourceBook = CostFactory.getAccessResourceBook((String) conf.get(IAccessResourceBook.ACCESS_RESOURCEBOOK), (String) conf.get(IAccessResourceBook.ACCESS_RESOURCEBOOK_PARAM));
		creditControl = CostFactory.getCreditControl((String) conf.get(ICreditControl.CREDIT_CONTROL), (String) conf.get(ICreditControl.CREDIT_CONTROL_PARAM));
	}

	@Override
	public Map<FeeTypeAndSubject, IRateFinder> getRate(ChargingDetailRecord record) {
		// do nothing
		return null;
	}

	@Override
	public List<ChargingDetailRecord> calculate(Map<FeeTypeAndSubject, IRateFinder> rateInfo, ChargingDetailRecord record) {
		accessResourceBook.reduce(getReduceSendData(record.getFields()), record);
		creditControl.send(getCreditSendData(record.getFields()), record);
		return Arrays.asList(record);
	}

	private String getCreditSendData(Map<String, String> fields) {
		Map<String, String> map = new HashMap<>();
		map.put(BillingConstants.EVENT_ID, fields.get(BillingConstants.SN));
		map.put(BillingConstants.SYSTEM_ID, fields.get(BillingConstants.SYSTEM_ID));
		map.put(BillingConstants.TENANT_ID, fields.get(BillingConstants.TENANT_ID));
		map.put(BillingConstants.SOURCE_TYPE, BillingConstants.SOURCE_TYPE_VALUE);
		map.put(BillingConstants.OWNER_TYPE, BillingConstants.OWNER_TYPE_SERV);
		map.put(BillingConstants.OWNER_ID, fields.get(BillingConstants.SUBS_ID));
		map.put(BillingConstants.EVENT_TYPE, BillingConstants.EVENT_TYPE_SUB_DATA);
		map.put(BillingConstants.AMOUNT, "0");
		map.put(BillingConstants.AMOUNT_MARK, BillingConstants.AMOUNT_MARK_MINUS);
		map.put(BillingConstants.AMOUNT_TYPE, BillingConstants.AMOUNT_TYPE_DATA);
		map.put(BillingConstants.EXPANDED_INFO, "{}");
		return jsonConverter.toJson(map);
	}

	private String getReduceSendData(Map<String, String> fields) {
		Map<String, String> map = new HashMap<>();
		map.put(BillingConstants.EVENT_ID, fields.get(BillingConstants.SN));
		map.put(BillingConstants.SYSTEM_ID, fields.get(BillingConstants.SYSTEM_ID));
		map.put(BillingConstants.TENANT_ID, fields.get(BillingConstants.TENANT_ID));
		map.put(BillingConstants.ACCT_ID, fields.get(BillingConstants.ACCT_ID));
		map.put(BillingConstants.SUBS_ID, fields.get(BillingConstants.SUBS_ID));
		BigDecimal upStream = new BigDecimal(Double.parseDouble(fields.get("up_stream")));
		logger.info("get down_stream................" + fields.get("down_stream"));
		BigDecimal downStream = new BigDecimal(Double.parseDouble(fields.get("down_stream")));
		map.put(BillingConstants.AMOUNT, upStream.add(downStream).setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString());
		map.put(BillingConstants.AMOUNT_TYPE, BillingConstants.AMOUNT_TYPE_DATA);
		return jsonConverter.toJson(map);
	}

}
