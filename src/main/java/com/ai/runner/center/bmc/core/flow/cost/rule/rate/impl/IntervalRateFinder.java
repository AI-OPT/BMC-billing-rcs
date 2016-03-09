package com.ai.runner.center.bmc.core.flow.cost.rule.rate.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.ai.runner.center.bmc.core.flow.cost.entity.ChargingDetailRecord;
import com.ai.runner.center.bmc.core.flow.cost.rule.IRateFinder;
import com.ai.runner.center.bmc.core.util.BillingConstants;

/**
 * 区间式费率
 * 
 * @author bixy
 *
 */
public abstract class IntervalRateFinder implements IRateFinder {
	private static Logger logger = Logger.getLogger(IntervalRateFinder.class);
	private List<Interval> intervals = new ArrayList<>();

	private TreeMap<String, Interval> keyPoints = new TreeMap<>();// key为区间的起始点

	public abstract BigDecimal getRate(ChargingDetailRecord chargingDetailRecord);

	public BigDecimal getRate(String value) {
		for (Interval interval : intervals) {
			if (interval.isInInterval(value)) {
				return interval.price;
			}
		}
		return null;
	}

	public BigDecimal getCheapestRate() {
		BigDecimal min = null;
		for (Interval interval : intervals) {
			if (min == null || min.compareTo(interval.getPrice()) == 1)
				min = interval.getPrice();
		}
		if (min == null) {
			logger.error("error find cheapest rate ......., set 0 instead !");
			min = new BigDecimal("0");
		}
		return min;
	}

	/**
	 * 是否为区间的起始点
	 * 
	 * @param value
	 * @return
	 */
	public boolean isKeyPoint(String value) {
		return keyPoints.containsKey(value);
	}

	/**
	 * 根据某个点获取区间的起始点
	 * 
	 * @param value
	 * @return
	 */
	public String getIntervalStart(String value) {
		if (isKeyPoint(value))
			return value;
		else {
			keyPoints.put(value, null);
			String result = keyPoints.lowerKey(value);
			keyPoints.remove(value);
			if (result == null)
				result = keyPoints.lastKey();
			return result;
		}
	}

	public String getPreIntervalStart(String value) {
		if (!isKeyPoint(value))
			return getIntervalStart(value);
		else {
			String lower = keyPoints.lowerKey(value);
			if (lower == null)
				lower = keyPoints.lastKey();
			return lower;
		}
	}

	public Interval getIntervalByStart(String key) {
		return keyPoints.get(key);
	}

	@Override
	public void setExtValue(BigDecimal price, String value) {
		String[] valueArr = value.split(BillingConstants.COMMON_SPLIT, -1);
		Interval interval = new Interval(price, valueArr[0], valueArr[1]);
		intervals.add(interval);
		keyPoints.put(valueArr[0], interval);
	}

	public abstract String getRateFinderType();

	public TreeMap<String, Interval> getKeyPoints() {
		return keyPoints;
	}

	public static class Interval {
		private BigDecimal price;
		private String start;
		private String end;

		public Interval(BigDecimal price, String start, String end) {
			this.price = price;
			this.start = start;
			this.end = end;
		}

		public boolean isInInterval(String value) {
			if (value.compareTo(start) > 0 && value.compareTo(end) <= 0)
				return true;
			else
				return false;
		}

		public BigDecimal getPrice() {
			return price;
		}

		public String getStart() {
			return start;
		}

		public String getEnd() {
			return end;
		}
	}
}
