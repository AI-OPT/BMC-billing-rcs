package com.ai.runner.center.bmc.core.flow.cost.entity;

import java.math.BigDecimal;

/**
 * 科目和批价信息
 * 
 * @author bixy
 *
 */
public class SubjectAndPrice {
	private String subjectCode;
	private BigDecimal price;

	public SubjectAndPrice(String subjectCode, BigDecimal price) {
		this.subjectCode = subjectCode;
		this.price = price;
	}

	public String getSubjectCode() {
		return subjectCode;
	}

	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

}
