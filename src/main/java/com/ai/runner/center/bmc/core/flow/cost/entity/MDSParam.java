package com.ai.runner.center.bmc.core.flow.cost.entity;

public class MDSParam extends PaasParam {
	private String topic;
	private Integer partition;

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public Integer getPartition() {
		return partition;
	}

	public void setPartition(Integer partition) {
		this.partition = partition;
	}

}
