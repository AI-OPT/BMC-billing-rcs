package com.ai.runner.center.bmc.core.persistence.entity;

import java.math.BigDecimal;

public class CpUnitpriceItem {
	private Integer id;
	private String feeItemCode;
	private Integer feeType;
	private String priceValue;
	private String unitType;
	private String subjectCode;
	private String itemExtCode;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getFeeType() {
		return feeType;
	}

	public void setFeeType(Integer feeType) {
		this.feeType = feeType;
	}

	public BigDecimal getPriceValueBigDecimal() {
		return new BigDecimal(priceValue);
	}

	public String getPriceValue() {
		return priceValue;
	}

	public void setPriceValue(String priceValue) {
		this.priceValue = priceValue;
	}

	public String getUnitType() {
		return unitType;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

	public String getSubjectCode() {
		return subjectCode;
	}

	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}

	public String getFeeItemCode() {
		return feeItemCode;
	}

	public void setFeeItemCode(String feeItemCode) {
		this.feeItemCode = feeItemCode;
	}

	public String getItemExtCode() {
		return itemExtCode;
	}

	public void setItemExtCode(String itemExtCode) {
		this.itemExtCode = itemExtCode;
	}

}
