package com.ai.runner.center.bmc.core.flow.cost.container;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.log4j.Logger;

import com.ai.paas.ipaas.mcs.CacheFactory;
import com.ai.paas.ipaas.mcs.interfaces.ICacheClient;
import com.ai.paas.ipaas.rcs.data.StreamData;
import com.ai.paas.ipaas.uac.vo.AuthDescriptor;
import com.ai.runner.center.bmc.core.entity.JdbcParam;
import com.ai.runner.center.bmc.core.exception.BillingPrepareException;
import com.ai.runner.center.bmc.core.flow.cost.AccountPeriodRule;
import com.ai.runner.center.bmc.core.flow.cost.AccountReriodRuleManager;
import com.ai.runner.center.bmc.core.flow.cost.RecordMappingRule;
import com.ai.runner.center.bmc.core.flow.cost.entity.AccuRule;
import com.ai.runner.center.bmc.core.flow.cost.entity.ChargingDetailRecord;
import com.ai.runner.center.bmc.core.flow.cost.entity.PaasParam;
import com.ai.runner.center.bmc.core.flow.cost.rule.BillingMaster;
import com.ai.runner.center.bmc.core.flow.cost.rule.impl.Unit;
import com.ai.runner.center.bmc.core.persistence.entity.BmcAccuRule;
import com.ai.runner.center.bmc.core.persistence.entity.BmcRecordFmt.BmcRecordFmtKey;
import com.ai.runner.center.bmc.core.util.BillingConstants;
import com.ai.runner.center.bmc.core.util.JdbcTemplate;
import com.ai.runner.center.bmc.core.util.Records;
import com.google.gson.Gson;

/**
 * 容器的代理类，负责处理所有的数据
 * 
 * @author bixy
 * 
 */
public class ContainerProxy {
	private static Logger logger = Logger.getLogger(BillingContainer.class);
	private static final String BMC_ACCU_RULE_SELECT = "select a.tenant_id tenantId,a.service_id serviceId,a.stat_id statId,a.table_name tableName,a.group_fields groupFields,a.accu_fields accuFields from bmc_accu_rule a where a.rule_type = 'fee'";
	private BillingMaster billingMaster;
	private RecordMappingRule mappingRule;
	private BillingContainer container; // 处理当前帐期的容器
	private Map<BmcRecordFmtKey, List<AccuRule>> accuRuleMap = new HashMap<>(); // 累计规则
	private ICacheClient icache;

	private AccountReriodRuleManager accountReriodRuleManager;

	public ContainerProxy(Map conf, int flushMetric, AccountReriodRuleManager accountReriodRuleManager) {
		billingMaster = new BillingMaster(conf);
		logger.debug("***************************load rules start**********************");
		loadrules(conf);
		logger.debug("load accu rule size " + accuRuleMap.size());
		JdbcParam jdbcParam = JdbcParam.getInstance(conf);
		Connection conn = null;
		try {
			conn = JdbcTemplate.getConnection(jdbcParam);
		} catch (ClassNotFoundException e) {
			throw new BillingPrepareException(e);
		} catch (SQLException e) {
			throw new BillingPrepareException(e);
		}
		mappingRule = RecordMappingRule.getMappingRuleBefore(RecordMappingRule.FORMAT_TYPE_OUTPUT, conn);
		logger.debug("***************************load rules finish**********************");

		container = new BillingContainer(accuRuleMap, flushMetric);
		this.accountReriodRuleManager = accountReriodRuleManager;
		
		prepareMcs(conf);
	}
	
	/**
	 * 构建连接mcs服务
	 */
	private void prepareMcs(Map conf) {
		PaasParam paasParam = (new Gson()).fromJson((String) conf.get(Unit.BILLING_UNIT_MCS_PARAM), PaasParam.class);
		AuthDescriptor ad = new AuthDescriptor(paasParam.getAuthAddr(), paasParam.getpId(), paasParam.getPassword(), paasParam.getSrvId());
		try {
			icache = CacheFactory.getClient(ad);
		} catch (Exception e) {
			throw new BillingPrepareException(e);
		}
	}
	
	private void increase(){
		icache.incr("cost-receive");
	}

	public void pushToStatistic(StreamData streamData) {
		String systemId = streamData.getString(0);
		String tenantId = streamData.getString(1);
		String serviceId = streamData.getString(2);
		String psn = streamData.getString(3);
		String sn = streamData.getString(4);
		String subsId = streamData.getString(5);
		String recordString = streamData.getString(6);

		logger.info("ContainerProxy receive data----->" + recordString);
		increase();
		BmcRecordFmtKey bmcRecordFmtKey = new BmcRecordFmtKey(tenantId, serviceId);
		Map<String, Integer> indexes = mappingRule.getIndexes(bmcRecordFmtKey);
		if (indexes == null) {
			logger.error("can't find mapping rule for " + bmcRecordFmtKey);
			logger.error("record is throw away !");
			return;
		}
		AccountPeriodRule accountPeriodRule = accountReriodRuleManager.getAccountPeriodRule(bmcRecordFmtKey);
		if (accountPeriodRule == null) {
			logger.error("couldn't find account period rule for " + bmcRecordFmtKey);
			logger.error("record is throw away !");
			return;
		}

		Records records = new Records(recordString, indexes);
		List<ChargingDetailRecord> values = billingMaster.billing(records, bmcRecordFmtKey, systemId, subsId, psn, sn);
		if (accountPeriodRule.isInSwithPeriod()) {
			for (ChargingDetailRecord chargingDetailRecord : values) {
				if (accountPeriodRule.isInCurrentAccountPeriod(chargingDetailRecord.get(BillingConstants.START_TIME))) {
					chargingDetailRecord.setAccountPeriod(accountPeriodRule.getCurrentAccountPeriod());
					container.pushToStatistic(chargingDetailRecord);
				} else {
					chargingDetailRecord.setAccountPeriod(accountPeriodRule.getLastAccountPeriod());
					container.pushToStatistic(chargingDetailRecord);
				}
			}
		} else {
			for (ChargingDetailRecord chargingDetailRecord : values) {
				chargingDetailRecord.setAccountPeriod(accountPeriodRule.getCurrentAccountPeriod());
				container.pushToStatistic(chargingDetailRecord);
			}
		}
	}

	/**
	 * 加载累帐规则
	 */
	private void loadrules(Map conf) {
		try {
			JdbcParam jdbcParam = JdbcParam.getInstance(conf);
			Connection connection = JdbcTemplate.getConnection(jdbcParam);

			List<BmcAccuRule> ruleList = JdbcTemplate.closeAfterQuery(BMC_ACCU_RULE_SELECT, connection, new BeanListHandler<BmcAccuRule>(BmcAccuRule.class));
			for (BmcAccuRule bmcAccuRule : ruleList) {
				BmcRecordFmtKey key = new BmcRecordFmtKey(bmcAccuRule.getTenantId(), bmcAccuRule.getServiceId());
				List<AccuRule> list = accuRuleMap.get(key);
				if (list == null) {
					list = new ArrayList<>();
					accuRuleMap.put(key, list);
				}
				AccuRule accuRule = new AccuRule(bmcAccuRule.getTableName());
				list.add(accuRule);
			}
		} catch (ClassNotFoundException e) {
			throw new BillingPrepareException(e);
		} catch (SQLException e) {
			throw new BillingPrepareException(e);
		}
	}

	public BillingContainer getContainer() {
		return container;
	}

	public boolean isNeedToFlush() {
		return container.isNeedToFlush();
	}
}
