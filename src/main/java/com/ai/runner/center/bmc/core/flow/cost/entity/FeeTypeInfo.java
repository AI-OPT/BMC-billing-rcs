package com.ai.runner.center.bmc.core.flow.cost.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.ai.runner.center.bmc.core.flow.cost.rule.IRateFinder;
import com.ai.runner.center.bmc.core.flow.cost.rule.rate.impl.TimeUnitRateFinder;
import com.ai.runner.center.bmc.core.persistence.entity.CpExtInfo;

/**
 * 管理不同feeType对应的不同的费率信息
 * 
 * @author bixy
 *
 */
public class FeeTypeInfo {
	private static Logger logger = Logger.getLogger(FeeTypeInfo.class);

	private Map<FeeTypeAndSubject, List<IRateFinder>> feePrices = new HashMap<>();

	/**
	 * 增加批价信息
	 * 
	 * @param feeType
	 * @param subjectCode
	 * @param priceValue
	 * @param extInfo
	 */
	public void putFeeTypeInfo(Integer feeType, String subjectCode, BigDecimal priceValue, List<CpExtInfo> extInfo) {
		FeeTypeAndSubject feeTypeAndSubject = new FeeTypeAndSubject(feeType, subjectCode);
		List<IRateFinder> finders = feePrices.get(feeTypeAndSubject);
		if (finders == null) {
			finders = new ArrayList<>();
			feePrices.put(feeTypeAndSubject, finders);
		}
		for (CpExtInfo cpExtInfo : extInfo) {
			IRateFinder rateFinder = null;
			for (IRateFinder finder : finders) {
				if (finder.getRateFinderType().equals(cpExtInfo.getExtName())) {
					rateFinder = finder;
					break;
				}
			}
			if (rateFinder == null) {
				rateFinder = newRateFinder(cpExtInfo.getExtName());
				if (rateFinder == null) {
					logger.error("ext code is " + cpExtInfo.getExtCode() + ",type is " + cpExtInfo.getExtName() + " unsupport now");
					continue;
				}
				finders.add(rateFinder);
			}
			rateFinder.setExtValue(priceValue, cpExtInfo.getExtValue());
		}
	}

	/**
	 * 获取适配到所有的批价信息
	 * 
	 * @param record
	 * @return
	 */
	public Map<FeeTypeAndSubject, IRateFinder> getRate(ChargingDetailRecord record) {
		Map<FeeTypeAndSubject, IRateFinder> result = new HashMap<>();
		for (Entry<FeeTypeAndSubject, List<IRateFinder>> entry : feePrices.entrySet()) {
			for (IRateFinder rateFinder : entry.getValue()) {
				BigDecimal price = rateFinder.getRate(record);
				if (price != null) {
					// 每种feeType仅仅取第一被适配到的批价
					result.put(entry.getKey(), rateFinder);
					break;
				}
			}
		}
		return result;
	}

	/**
	 * 获取最小费率的映射集合
	 * 
	 * @return
	 */
	public Map<FeeTypeAndSubject, IRateFinder> getMinRate() {
		Map<FeeTypeAndSubject, IRateFinder> result = new HashMap<>();
		for (Entry<FeeTypeAndSubject, List<IRateFinder>> entry : feePrices.entrySet()) {
			BigDecimal minPrice = null;
			for (IRateFinder rateFinder : entry.getValue()) {
				BigDecimal price = rateFinder.getCheapestRate();
				if (price != null) {
					if (minPrice == null || minPrice.compareTo(price) == 1) {
						minPrice = price;
						result.put(entry.getKey(), rateFinder);
					}
				}
			}
		}
		return result;
	}

	private IRateFinder newRateFinder(String rateFinderType) {
		if (rateFinderType.equals(TimeUnitRateFinder.RATE_FINDER_TYPE)) {
			return new TimeUnitRateFinder();
		}
		return null;
	}

	public Map<FeeTypeAndSubject, List<IRateFinder>> getFeePrices() {
		return feePrices;
	}

}
