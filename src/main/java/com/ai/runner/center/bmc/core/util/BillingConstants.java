package com.ai.runner.center.bmc.core.util;

public class BillingConstants {
	public static final String BILLING_HBASE_PARAM = "billing.hbase.param";
	
	public static final String FIELD_SPLIT = new String(new char[] { (char) 1 });
	public static final String RECORD_SPLIT = new String(new char[] { (char) 2 });
	public static final String COMMON_SPLIT = ",";
	public static final String COMMON_JOINER = "_";
	public static final String COMMON_HYPHEN = "-";

	public static final String RECORD_DATA = "record_data";
	public static final String RECORD_HANGING = "record_hanging";

	public static final String FEE_ITEM_CODE = "fee_item_code";
	public static final String CAL_TYPE = "cal_type";
	public static final String CONSUMPTION = "consumption";
	public static final String FEE = "fee";
	public static final String SYSTEM_ID = "system_id";
	public static final String TENANT_ID = "tenant_id";
	public static final String SERVICE_ID = "service_id";
	public static final String PSN = "psn";
	public static final String SN = "sn";
	public static final String CREATE_DATE = "create_date";
	public static final String SUBS_ID = "subs_id";
	public static final String ACCT_ID = "acct_id";
	public static final String SUBJECT = "subject";
	public static final String SUBJECT_ID = "subject_id";
	public static final String TOTAL = "total";
	public static final String SOURCE_TYPE = "source_type";
	public static final String SOURCE_TYPE_VALUE = "3";
	public static final String OWNER_TYPE = "owner_type";
	public static final String OWNER_TYPE_SERV = "serv";
	public static final String OWNER_TYPE_ACCT = "acct";
	public static final String OWNER_ID = "owner_id";
	public static final String EVENT_TYPE = "event_type";
	public static final String EVENT_TYPE_SUB_DATA = "60";
	public static final String EVENT_TYPE_MAIN = "00";
	public static final String EVENT_ID = "event_id";
	public static final String AMOUNT = "amount";
	public static final String AMOUNT_MARK = "amount_mark";
	public static final String AMOUNT_MARK_MINUS = "MINUS";
	public static final String AMOUNT_TYPE = "amount_type";
	public static final String AMOUNT_TYPE_DATA = "DATA";
	public static final String AMOUNT_TYPE_BOOK = "BOOK";
	public static final String START_TIME = "start_time";
	public static final String END_TIME = "end_time";
	
	public static final String EXPANDED_INFO ="expanded_info";
	public static final String CHARGING_PILE = "charging_pile";
	public static final String CHARGING_STATION = "charging_station";
	
	public static final String UNPACKING_PROCESSOR ="unpacking";
	public static final String DATA_QUALITY_PROCESSOR = "data_quality";
	public static final String RULE_ADAPT_PROCESSOR = "rule_adapt";
}
