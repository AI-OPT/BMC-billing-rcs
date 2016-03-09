package com.ai.runner.center.bmc.core.flow.cost.entity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 累计规则
 * 
 * @author bixy
 * 
 */
public class AccuRule {
	private static final String ID = "id";
	private static final String[] ID_GROUP = { "tenant_id", "subs_id", "subject_id" };
	private String tableName;
	private String[] groupField = { "id", "system_id", "tenant_id", "acct_id", "subs_id", "service_num", "subject_id","service_id" };
	private String[] accuField = { "total" };
	private String updateSql;
	private String insertSql;

	public AccuRule(String tableName) {
		this.tableName = tableName;
		buildUpdateSql();
		buildInsertSql();
	}

	private void buildUpdateSql() {
		StringBuilder sqlBuilder = new StringBuilder("update %s set ");
		for (String name : accuField) {
			sqlBuilder.append(name + "=" + name + " + %.6f,");
		}
		sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
		sqlBuilder.append(" where ");
		sqlBuilder.append(ID + "='%s' ");
		this.updateSql = sqlBuilder.toString();
	}

	private void buildInsertSql() {
		StringBuilder sqlBuilder = new StringBuilder("insert into %s(");
		for (String name : accuField) {
			sqlBuilder.append(name + ",");
		}
		for (String name : groupField) {
			sqlBuilder.append(name + ",");
		}
		sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
		sqlBuilder.append(") values(");
		for (int i = 0; i < accuField.length; i++) {
			sqlBuilder.append("%.6f,");
		}
		for (int i = 0; i < groupField.length; i++) {
			sqlBuilder.append("'%s',");
		}
		sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
		sqlBuilder.append(")");
		this.insertSql = sqlBuilder.toString();
	}

	public String getUpdateSql(GroupFieldValue groupFieldValue, AccuFieldValue accuFieldValue) {
		return String.format(updateSql, buildUpdateArgs(groupFieldValue, accuFieldValue));
	}

	private Object[] buildUpdateArgs(GroupFieldValue groupFieldValue, AccuFieldValue accuFieldValue) {
		List<Object> args = new ArrayList<>();
		args.add(groupFieldValue.getTableName());
		args.addAll(accuFieldValue.getDoubleValues());
		args.add(groupFieldValue.getId());
		return args.toArray();
	}

	public String getInsertSql(GroupFieldValue groupFieldValue, AccuFieldValue accuFieldValue) {
		return String.format(insertSql, buildInsertArgs(groupFieldValue, accuFieldValue));
	}

	private Object[] buildInsertArgs(GroupFieldValue groupFieldValue, AccuFieldValue accuFieldValue) {
		List<Object> args = new ArrayList<>();
		args.add(groupFieldValue.getTableName());
		args.addAll(accuFieldValue.getDoubleValues());
		args.addAll(groupFieldValue.getField());
		return args.toArray();
	}

	public GroupFieldValue getGroupFieldValue(ChargingDetailRecord chargingDetailRecord) {
		GroupFieldValue groupFieldValue = new GroupFieldValue();
		groupFieldValue.setAccuRule(this);
		groupFieldValue.setTableName(tableName + chargingDetailRecord.getAccountPeriod());
		List<String> fieldValues = new ArrayList<>();
		for (String fieldName : groupField) {
			if (ID.equals(fieldName)) {
				StringBuilder id = new StringBuilder("");
				for (String idg : ID_GROUP) {
					id.append(chargingDetailRecord.get(idg));
				}
				fieldValues.add(id.toString());
			} else
				fieldValues.add(chargingDetailRecord.get(fieldName));
		}
		groupFieldValue.setField(fieldValues);
		return groupFieldValue;
	}

	public AccuFieldValue getAccuRuleValue(Map<String, String> map) {
		AccuFieldValue accuFieldValue = new AccuFieldValue();
		accuFieldValue.setAccuRule(this);
		List<BigDecimal> fieldValues = new ArrayList<>();
		for (String fieldName : accuField) {
			fieldValues.add(new BigDecimal(map.get(fieldName)));
		}
		accuFieldValue.setValues(fieldValues);
		return accuFieldValue;
	}

	public String getTableName() {
		return tableName;
	}

}
