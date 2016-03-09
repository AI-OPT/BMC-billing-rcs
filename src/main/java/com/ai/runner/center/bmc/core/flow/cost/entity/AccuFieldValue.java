package com.ai.runner.center.bmc.core.flow.cost.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccuFieldValue {
	private AccuRule accuRule;
	private List<BigDecimal> values;

	public void add(AccuFieldValue accuFieldValue) {
		for (int i = 0; i < values.size(); i++) {
			BigDecimal source = values.get(i);
			source = source.add(accuFieldValue.getValues().get(i));
			values.set(i, source);
		}
	}

	public List<Double> getDoubleValues() {
		List<Double> result = new ArrayList<>(values.size());
		for (BigDecimal value : values) {
			result.add(value.doubleValue());
		}
		return result;
	}

	public List<BigDecimal> getValues() {
		return values;
	}

	public void setValues(List<BigDecimal> values) {
		this.values = values;
	}

	public AccuRule getAccuRule() {
		return accuRule;
	}

	public void setAccuRule(AccuRule accuRule) {
		this.accuRule = accuRule;
	}

}
