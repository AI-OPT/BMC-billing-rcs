package com.ai.runner.center.bmc.core.flow.cost.entity;

import java.util.ArrayList;
import java.util.List;

import com.ai.runner.center.bmc.core.util.BillingConstants;

/**
 * @author bixy
 * 
 */
public class AccuField {
	private String[] accuField = { "fee1", "fee2", "fee3" };
	private List<String> fieldNames = new ArrayList<>();

	public AccuField(String name) {
		String[] names = name.split(BillingConstants.COMMON_SPLIT, -1);
		for (int i = 0; i < names.length; i++) {
			fieldNames.add(names[i]);
		}
	}

	public List<String> getFieldNames() {
		return fieldNames;
	}

	public void setFieldNames(List<String> fieldNames) {
		this.fieldNames = fieldNames;
	}

}
