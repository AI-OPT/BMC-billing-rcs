package com.ai.runner.center.bmc.core.flow.cost;

import java.util.List;

import com.ai.paas.ipaas.rcs.data.StreamData;

public class TestStreamData extends StreamData {
	private List<String> values;

	public TestStreamData(List<String> values) {
		this.values = values;
	}

	public String getString(int i) {
		return values.get(i);
	}
}
