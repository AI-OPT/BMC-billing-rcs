package com.ai.runner.center.bmc.core.flow.cost;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.Logger;

/**
 * 负责处理帐期相关的类
 * 
 * @author bixy
 * 
 */
public class AccountPeriodRule {
	private static Logger logger = Logger.getLogger(AccountPeriodRule.class);
	private String lastAccountPeriod; // 上一帐期，记录处于切换期内的上一次帐期
	private String currentAccountPeriod; // 当期帐期
	private AtomicBoolean inSwithPeriod; // 是否处于切换期内
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
	private Calendar calendar = Calendar.getInstance();
	private int switchHour;

	public AccountPeriodRule(int switchHour) {
		this.switchHour = switchHour;
		Calendar now = Calendar.getInstance();
		int today = now.get(Calendar.DAY_OF_MONTH);
		int nowHour = now.get(Calendar.HOUR_OF_DAY);
		if (switchHour > 0 && today == 1 && nowHour < switchHour) {
			currentAccountPeriod = sdf.format(now.getTime());
			try {
				calendar.setTime(sdf.parse(currentAccountPeriod));
				calendar.add(Calendar.MONTH, -1);
				lastAccountPeriod = sdf.format(calendar.getTime());
				inSwithPeriod = new AtomicBoolean(true);
			} catch (ParseException e) {
				logger.error("error parse currentAccountPeriod", e);
			}
		} else {

			currentAccountPeriod = sdf.format(now.getTime());
			lastAccountPeriod = "";
			inSwithPeriod = new AtomicBoolean(false);
		}
	}
	
	/**
	 * 是否处于切帐调整期
	 * 
	 * @return
	 */
	public synchronized boolean isInSwithPeriod() {
		return inSwithPeriod.get();
	}

	public synchronized boolean isInCurrentAccountPeriod(String date) {
		return currentAccountPeriod.equals(date.substring(0, 5));
	}

	public synchronized String getLastAccountPeriod() {
		return lastAccountPeriod;
	}

	public synchronized String getCurrentAccountPeriod() {
		return currentAccountPeriod;
	}

	/**
	 * 切换到换月调账期
	 */
	public synchronized void changeToSwith() {
		logger.info("change to switch period.......");
		lastAccountPeriod = currentAccountPeriod;
		try {
			calendar.setTime(sdf.parse(currentAccountPeriod));
			calendar.add(Calendar.MONTH, 1);
			currentAccountPeriod = sdf.format(calendar.getTime());
		} catch (ParseException e) {
			logger.error("error parse currentAccountPeriod", e);
		}
		inSwithPeriod.set(true);
	}

	/**
	 * 切换到正常状态的账期
	 */
	public synchronized void changeToNormal() {
		logger.info("change to normal period.......");
		lastAccountPeriod = "";
		inSwithPeriod.set(false);
	}

	/**
	 * 正常切换账期
	 */
	public synchronized void changeToNextAccountPeriod() {
		try {
			calendar.setTime(sdf.parse(currentAccountPeriod));
			calendar.add(Calendar.MONTH, 1);
			currentAccountPeriod = sdf.format(calendar.getTime());
		} catch (ParseException e) {
			logger.error("error parse currentAccountPeriod", e);
		}
	}

	public int getSwitchHour() {
		return switchHour;
	}

}
