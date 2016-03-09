package com.ai.runner.center.bmc.core.flow.cost.entity;

import java.util.List;

/**
 * 分组字段值
 * 
 * @author bixy
 * 
 */
public class GroupFieldValue {
	private AccuRule accuRule;
	private String tableName;
	private List<String> field;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((field == null) ? 0 : field.hashCode());
		result = prime * result + ((tableName == null) ? 0 : tableName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GroupFieldValue other = (GroupFieldValue) obj;
		if (field == null) {
			if (other.field != null)
				return false;
		} else if (!field.equals(other.field))
			return false;
		if (tableName == null) {
			if (other.tableName != null)
				return false;
		} else if (!tableName.equals(other.tableName))
			return false;
		return true;
	}

	public String getId() {
		return field.get(0);
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<String> getField() {
		return field;
	}

	public void setField(List<String> field) {
		this.field = field;
	}

	public AccuRule getAccuRule() {
		return accuRule;
	}

	public void setAccuRule(AccuRule accuRule) {
		this.accuRule = accuRule;
	}

}
