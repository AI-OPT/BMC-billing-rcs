package com.ai.runner.center.bmc.core.flow.cost.entity;

import java.util.ArrayList;
import java.util.List;

import com.ai.runner.center.bmc.core.util.BillingConstants;

public class GroupField {
	private String[] groupField = { "business_id", "tenant_id", "cust_id", "subs_id" };
	private List<String> fieldNames = new ArrayList<>();

	public GroupField(String name) {
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
