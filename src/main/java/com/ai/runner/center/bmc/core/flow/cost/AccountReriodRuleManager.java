package com.ai.runner.center.bmc.core.flow.cost;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import com.ai.runner.center.bmc.core.entity.JdbcParam;
import com.ai.runner.center.bmc.core.exception.BillingPrepareException;
import com.ai.runner.center.bmc.core.flow.cost.quartz.ChangeNormalJob;
import com.ai.runner.center.bmc.core.flow.cost.quartz.ChangeSwitchJob;
import com.ai.runner.center.bmc.core.persistence.entity.BmcAcctmonthPara;
import com.ai.runner.center.bmc.core.persistence.entity.BmcRecordFmt.BmcRecordFmtKey;
import com.ai.runner.center.bmc.core.util.JdbcTemplate;

/**
 * 账期管理
 * 
 * @author bixy
 *
 */
public class AccountReriodRuleManager {
	private static Logger logger = Logger.getLogger(AccountReriodRuleManager.class);
	private static final String RULESQL = "select a.tenant_id tenantId,a.service_id serviceId,a.start_month startMonth,a.end_month endMonth,a.time_delay timeDelay,a.status status from bmc_acctmonth_para a where a.status = 'current'";
	private Map<BmcRecordFmtKey, AccountPeriodRule> AccountPeriodRuleMap = new HashMap<>();
	private Scheduler scheduler;

	public AccountReriodRuleManager(Scheduler scheduler, JdbcParam jdbcParam) {
		this.scheduler = scheduler;
		loadRule(jdbcParam);
		logger.debug("load account period " + AccountPeriodRuleMap.size());
	}

	public AccountPeriodRule getAccountPeriodRule(BmcRecordFmtKey bmcRecordFmtKey) {
		AccountPeriodRule accountPeriodRule = AccountPeriodRuleMap.get(bmcRecordFmtKey);
		return accountPeriodRule;
	}

	public Map<BmcRecordFmtKey, AccountPeriodRule> getAccountPeriodRuleMap() {
		return AccountPeriodRuleMap;
	}

	private void loadRule(JdbcParam jdbcParam) {
		try {
			Connection connection = JdbcTemplate.getConnection(jdbcParam);
			List<BmcAcctmonthPara> acctmonthList = JdbcTemplate.closeAfterQuery(RULESQL, connection, new BeanListHandler<BmcAcctmonthPara>(BmcAcctmonthPara.class));
			System.out.println("------------------" + acctmonthList.size());
			Map<AccountPeriodRule, List<BmcRecordFmtKey>> rulemap = new HashMap<>();
			for (BmcAcctmonthPara bmcAcctmonthPara : acctmonthList) {
				System.out.println(bmcAcctmonthPara);
				int timeDelay = bmcAcctmonthPara.getTimeDelay();
				// 如果切换期小于0（不正确设置），或者超过20天，将被置为默认值8小时
				if (timeDelay < 0 || timeDelay > 24 * 20)
					timeDelay = 8;
				AccountPeriodRule accountPeriodRule = new AccountPeriodRule(bmcAcctmonthPara.getTimeDelay());
				List<BmcRecordFmtKey> keyList = rulemap.get(accountPeriodRule);
				if (keyList == null) {
					keyList = new ArrayList<>();
					rulemap.put(accountPeriodRule, keyList);
				}
				keyList.add(new BmcRecordFmtKey(bmcAcctmonthPara.getTenantId(), bmcAcctmonthPara.getServiceId()));
			}
			try {
				startQuartzJobs(rulemap.keySet());
			} catch (SchedulerException e) {
				logger.error("", e);
			}
			for (Entry<AccountPeriodRule, List<BmcRecordFmtKey>> entry : rulemap.entrySet()) {
				for (BmcRecordFmtKey bmcRecordFmtKey : entry.getValue())
					AccountPeriodRuleMap.put(bmcRecordFmtKey, entry.getKey());
			}
		} catch (ClassNotFoundException e) {
			throw new BillingPrepareException(e);
		} catch (SQLException e) {
			throw new BillingPrepareException(e);
		}
	}

	private void startQuartzJobs(Set<AccountPeriodRule> ruleSet) throws SchedulerException {
		// 创建进入切换期定时任务，每个月1号的0时0分0秒进入切换期
		JobDataMap changeSwitchJobDataMap = new JobDataMap();
		changeSwitchJobDataMap.put(AccountPeriodRule.class.getName(), ruleSet);
		JobDetail changeSwitchJobDetail = JobBuilder.newJob(ChangeSwitchJob.class).usingJobData(changeSwitchJobDataMap).build();
		Trigger changeSwitchTrigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 1 * ?")).build();
		scheduler.scheduleJob(changeSwitchJobDetail, changeSwitchTrigger);

		// 创建关闭切换期定时任务，在设定的切换时长后，退出切换期
		for (AccountPeriodRule accountPeriodRule : ruleSet) {
			if (accountPeriodRule.getSwitchHour() == 0)
				continue;

			// 创建关闭切换期定时任务，在设定的切换时长后，退出切换期
			JobDataMap changeNormalJobDataMap = new JobDataMap();
			changeNormalJobDataMap.put(AccountPeriodRule.class.getName(), accountPeriodRule);
			JobDetail changeNormalJobDetail = JobBuilder.newJob(ChangeNormalJob.class).usingJobData(changeNormalJobDataMap).build();
			Trigger changeNormalTrigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule("0 0 " + analyseTimeDelay(accountPeriodRule.getSwitchHour()) + " * ?")).build();
			scheduler.scheduleJob(changeNormalJobDetail, changeNormalTrigger);
		}
	}

	private String analyseTimeDelay(int hours) {
		int day = hours / 24 + 1;
		int hour = hours % 24;
		return hour + " " + day;
	}
}
