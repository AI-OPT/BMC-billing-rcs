package com.ai.runner.center.bmc.core.flow.cost.entity;

public class FeeTypeAndSubject {
	private Integer feeType;
	private String subject;

	public FeeTypeAndSubject(Integer feeType, String subject) {
		this.feeType = feeType;
		this.subject = subject;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((feeType == null) ? 0 : feeType.hashCode());
		result = prime * result + ((subject == null) ? 0 : subject.hashCode());
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
		FeeTypeAndSubject other = (FeeTypeAndSubject) obj;
		if (feeType == null) {
			if (other.feeType != null)
				return false;
		} else if (!feeType.equals(other.feeType))
			return false;
		if (subject == null) {
			if (other.subject != null)
				return false;
		} else if (!subject.equals(other.subject))
			return false;
		return true;
	}

	public Integer getFeeType() {
		return feeType;
	}

	public void setFeeType(Integer feeType) {
		this.feeType = feeType;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

}
