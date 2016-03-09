package com.ai.runner.center.bmc.core.flow.cost.rule.rate.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.ai.runner.center.bmc.core.flow.cost.entity.ChargingDetailRecord;
import com.ai.runner.center.bmc.core.flow.cost.rule.IRateFinder;
import com.ai.runner.center.bmc.core.util.BillingConstants;

/**
 * 根据结束时间范围查找费率
 * 
 * @author bixy
 *
 */
public class TimeUnitRateFinder extends IntervalRateFinder implements IRateFinder {
	public static final String RATE_FINDER_TYPE = "TIME_SEG";

	@Override
	public BigDecimal getRate(ChargingDetailRecord chargingDetailRecord) {
		return super.getRate(getFormatTime(chargingDetailRecord));
	}

	public String getIntervalStart(ChargingDetailRecord chargingDetailRecord) {
		return super.getIntervalStart(getFormatTime(chargingDetailRecord));
	}

	public boolean isKeyPoint(ChargingDetailRecord chargingDetailRecord) {
		return super.isKeyPoint(getFormatTime(chargingDetailRecord));
	}

	public String getPreIntervalStart(ChargingDetailRecord chargingDetailRecord) {
		return super.getPreIntervalStart(getFormatTime(chargingDetailRecord));
	}

	/**
	 * 获取格式化的时间
	 * 
	 * @param chargingDetailRecord
	 * @return
	 */
	private String getFormatTime(ChargingDetailRecord chargingDetailRecord) {
		return getFormatTime(chargingDetailRecord, BillingConstants.END_TIME);
	}

	public String getFormatTime(ChargingDetailRecord chargingDetailRecord, String key) {
		String endTime = chargingDetailRecord.get(key);
		String formatEndTime = endTime.substring(8, 12) + "00";
		if ("000000".equals(formatEndTime))
			formatEndTime = "240000";
		return formatEndTime;
	}

	/**
	 * 获取关键时间
	 * 
	 * @return
	 */
	public List<String> getKeyTime() {
		List<String> keyTime = new ArrayList<>();
		return keyTime;
	}

	@Override
	public String getRateFinderType() {
		return RATE_FINDER_TYPE;
	}

}
