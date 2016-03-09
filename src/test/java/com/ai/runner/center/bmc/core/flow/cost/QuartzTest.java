package com.ai.runner.center.bmc.core.flow.cost;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.ai.runner.center.bmc.core.flow.cost.quartz.ChangeSwitchJob;

public class QuartzTest {
	public static void main(String[] args) throws SchedulerException{
		Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
		JobDetail changeSwitchJobDetail = JobBuilder.newJob(TESTJOB.class).build();
		Trigger changeSwitchTrigger = TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?")).build();
		scheduler.scheduleJob(changeSwitchJobDetail, changeSwitchTrigger);
		scheduler.start();
	}
	
	public static class TESTJOB implements Job{

		@Override
		public void execute(JobExecutionContext context) throws JobExecutionException {
			System.out.println("---------------------------------"+System.currentTimeMillis());
		}
		
	}
}
