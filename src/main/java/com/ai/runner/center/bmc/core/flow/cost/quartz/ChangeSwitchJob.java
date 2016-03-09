package com.ai.runner.center.bmc.core.flow.cost.quartz;

import java.util.Set;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ai.runner.center.bmc.core.flow.cost.AccountPeriodRule;

public class ChangeSwitchJob implements Job {

	@SuppressWarnings("unchecked")
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		Set<AccountPeriodRule> rule = (Set<AccountPeriodRule>) context.getJobDetail().getJobDataMap().get(AccountPeriodRule.class.getName());
		for (AccountPeriodRule accountPeriodRule : rule) {
			if (accountPeriodRule.getSwitchHour() == 0)
				accountPeriodRule.changeToNextAccountPeriod();
			else
				accountPeriodRule.changeToSwith();
		}
	}
}
