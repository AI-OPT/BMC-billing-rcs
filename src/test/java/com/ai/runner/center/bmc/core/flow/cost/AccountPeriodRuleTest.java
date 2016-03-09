package com.ai.runner.center.bmc.core.flow.cost;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.ai.runner.center.bmc.core.flow.cost.quartz.ChangeNormalJob;
import com.ai.runner.center.bmc.core.flow.cost.quartz.ChangeSwitchJob;

public class AccountPeriodRuleTest {

	public static void main(String[] args) {
		try {
			AccountPeriodRule accountPeriodRule = new AccountPeriodRule(9);
			ReqThread reqThread = new ReqThread(accountPeriodRule);
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
			JobDataMap jobDataMap = new JobDataMap();
			jobDataMap.put(AccountPeriodRule.class.getName(), accountPeriodRule);
			JobDetail changeSwitchJobDetail = JobBuilder.newJob(ChangeSwitchJob.class).usingJobData(jobDataMap).build();
			JobDetail changeNormalJobDetail = JobBuilder.newJob(ChangeNormalJob.class).usingJobData(jobDataMap).build();
			Trigger changeSwitchTrigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule("0 * * * * ?")).build();
			Trigger changeNormalTrigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule("20 * * * * ?")).build();
			scheduler.scheduleJob(changeSwitchJobDetail, changeSwitchTrigger);
			scheduler.scheduleJob(changeNormalJobDetail, changeNormalTrigger);
			scheduler.start();
			new Thread(reqThread).start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	private static class ReqThread implements Runnable {
		private AccountPeriodRule accountPeriodRule;

		public ReqThread(AccountPeriodRule accountPeriodRule) {
			this.accountPeriodRule = accountPeriodRule;
		}

		@Override
		public void run() {
			while (true) {
				System.out.println("********************************************");
				System.out.println(accountPeriodRule.isInSwithPeriod());
				System.out.println(accountPeriodRule.getCurrentAccountPeriod());
				if (accountPeriodRule.isInSwithPeriod()) {
					System.out.println(accountPeriodRule.getLastAccountPeriod());
				}
				System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++");
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}
}
