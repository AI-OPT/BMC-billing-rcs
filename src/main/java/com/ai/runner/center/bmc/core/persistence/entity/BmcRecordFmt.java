package com.ai.runner.center.bmc.core.persistence.entity;

import com.ai.runner.center.bmc.core.util.BillingConstants;

public class BmcRecordFmt {
	private BmcRecordFmtKey bmcRecordFmtKey;
	private String tenantId;
	private String serviceId;
	private String formatType;
	private Integer fieldSerial;
	private String fieldName;
	private String fieldCode;
	private String dataType;

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getFormatType() {
		return formatType;
	}

	public void setFormatType(String formatType) {
		this.formatType = formatType;
	}

	public Integer getFieldSerial() {
		return fieldSerial;
	}

	public void setFieldSerial(Integer fieldSerial) {
		this.fieldSerial = fieldSerial;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldCode() {
		return fieldCode;
	}

	public void setFieldCode(String fieldCode) {
		this.fieldCode = fieldCode;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public static class BmcRecordFmtKey {
		private String tenantId;
		private String busiType;

		public BmcRecordFmtKey(String tenantId, String busiType) {
			this.tenantId = tenantId;
			this.busiType = busiType;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((busiType == null) ? 0 : busiType.hashCode());
			result = prime * result + ((tenantId == null) ? 0 : tenantId.hashCode());
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
			BmcRecordFmtKey other = (BmcRecordFmtKey) obj;
			if (busiType == null) {
				if (other.busiType != null)
					return false;
			} else if (!busiType.equals(other.busiType))
				return false;
			if (tenantId == null) {
				if (other.tenantId != null)
					return false;
			} else if (!tenantId.equals(other.tenantId))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return tenantId + BillingConstants.COMMON_SPLIT + busiType;
		}

		public String toJoinString() {
			return tenantId + BillingConstants.COMMON_JOINER + busiType;
		}

		public String getTenantId() {
			return tenantId;
		}

		public void setTenantId(String tenantId) {
			this.tenantId = tenantId;
		}

		public String getBusiType() {
			return busiType;
		}

		public void setBusiType(String busiType) {
			this.busiType = busiType;
		}

	}

	public BmcRecordFmtKey getBmcRecordFmtKey() {
		if (bmcRecordFmtKey == null)
			bmcRecordFmtKey = new BmcRecordFmtKey(tenantId, serviceId);
		return bmcRecordFmtKey;
	}
}
