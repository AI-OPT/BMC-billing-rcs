package com.ai.runner.center.bmc.core.flow.cost;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.ai.runner.center.bmc.core.flow.cost.credit.ICreditControl;
import com.ai.runner.center.bmc.core.flow.cost.entity.FeeTypeAndSubject;
import com.ai.runner.center.bmc.core.flow.cost.entity.FeeTypeInfo;
import com.ai.runner.center.bmc.core.flow.cost.resourcebook.IAccessResourceBook;
import com.ai.runner.center.bmc.core.flow.cost.rule.IRateFinder;
import com.ai.runner.center.bmc.core.flow.cost.rule.impl.Unit;
import com.ai.runner.center.bmc.core.flow.cost.rule.rate.impl.TimeUnitRateFinder;
import com.ai.runner.center.bmc.core.flow.cost.rule.rate.impl.IntervalRateFinder.Interval;

public class TimeUnitRateTest {

	@Test
	public void rateTest() {
		Map conf = new HashMap<>();
		conf.put(IAccessResourceBook.ACCESS_RESOURCEBOOK, "print");
		conf.put(ICreditControl.CREDIT_CONTROL, "print");
		Unit unit = new Unit(conf);
		Map<String, FeeTypeInfo> rateMap = unit.getRateMap();
		System.out.println(rateMap.size());
		for (Entry<String, FeeTypeInfo> entry : rateMap.entrySet()) {
			System.out.println("key--------------" + entry.getKey());
			for (Entry<FeeTypeAndSubject, List<IRateFinder>> entry1 : entry.getValue().getFeePrices().entrySet()) {
				System.out.println("++++++++++++++++++++" + entry1.getKey().getFeeType() + "-------" + entry1.getKey().getSubject());
				for (IRateFinder rateFinder : entry1.getValue())
					if (rateFinder instanceof TimeUnitRateFinder) {
						TimeUnitRateFinder trf = (TimeUnitRateFinder) rateFinder;
						System.out.println(trf.getKeyPoints().size());
						for (Entry<String, Interval> entry2 : trf.getKeyPoints().entrySet()) {

							System.out.println("**********" + entry2.getKey() + "*********" + entry2.getValue().getEnd() + "**********" + entry2.getValue().getPrice());
						}
					} else {
						System.out.println(rateFinder.getClass());
					}
			}
		}
	}
}
