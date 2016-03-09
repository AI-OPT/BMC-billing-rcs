package com.ai.runner.center.bmc.core.flow.cost.entity;

import java.math.BigDecimal;

/**
 * 科目以及费用信息
 * 
 * @author bixy
 *
 */
public class SubjectAndPriceValue {
	private String subjectCode;
	private BigDecimal priceValue;

	public SubjectAndPriceValue(SubjectAndPrice subjectAndPrice, BigDecimal quantity) {
		this.subjectCode = subjectAndPrice.getSubjectCode();
		this.priceValue = subjectAndPrice.getPrice().multiply(quantity);
	}

	public String getSubjectCode() {
		return subjectCode;
	}

	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}

	public BigDecimal getPriceValue() {
		return priceValue;
	}

	public void setPriceValue(BigDecimal priceValue) {
		this.priceValue = priceValue;
	}

}
