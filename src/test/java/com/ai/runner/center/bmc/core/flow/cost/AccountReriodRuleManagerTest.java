package com.ai.runner.center.bmc.core.flow.cost;

import java.util.Map;

import org.junit.Test;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import com.ai.runner.center.bmc.core.entity.JdbcParam;
import com.ai.runner.center.bmc.core.persistence.entity.BmcRecordFmt.BmcRecordFmtKey;

public class AccountReriodRuleManagerTest {

	@Test
	public void testRuleMap() throws SchedulerException {
		JdbcParam jdbcParam = new JdbcParam("com.mysql.jdbc.Driver", "jdbc:mysql://10.1.234.163:3306/ebilling?useUnicode=true&characterEncoding=UTF-8", "inv", "inv");
		AccountReriodRuleManager manager = new AccountReriodRuleManager(StdSchedulerFactory.getDefaultScheduler(),jdbcParam);
		Map<BmcRecordFmtKey, AccountPeriodRule> map = manager.getAccountPeriodRuleMap();
		System.out.println(map.size());
	}
}
