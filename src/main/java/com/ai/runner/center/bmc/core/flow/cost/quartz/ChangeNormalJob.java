package com.ai.runner.center.bmc.core.flow.cost.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ai.runner.center.bmc.core.flow.cost.AccountPeriodRule;

public class ChangeNormalJob implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		AccountPeriodRule rule = (AccountPeriodRule)context.getJobDetail().getJobDataMap().get(AccountPeriodRule.class.getName());
		rule.changeToNormal();
	}

}
