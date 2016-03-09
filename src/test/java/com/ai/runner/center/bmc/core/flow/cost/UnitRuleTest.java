package com.ai.runner.center.bmc.core.flow.cost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

import com.ai.paas.ipaas.mcs.CacheFactory;
import com.ai.paas.ipaas.mcs.interfaces.ICacheClient;
import com.ai.paas.ipaas.rcs.data.StreamData;
import com.ai.paas.ipaas.uac.vo.AuthDescriptor;
import com.ai.runner.center.bmc.core.entity.JdbcParam;
import com.ai.runner.center.bmc.core.flow.cost.container.ContainerProxy;
import com.ai.runner.center.bmc.core.flow.cost.credit.ICreditControl;
import com.ai.runner.center.bmc.core.flow.cost.entity.AccuFieldValue;
import com.ai.runner.center.bmc.core.flow.cost.entity.ChargingDetailRecord;
import com.ai.runner.center.bmc.core.flow.cost.entity.GroupFieldValue;
import com.ai.runner.center.bmc.core.flow.cost.resourcebook.IAccessResourceBook;
import com.ai.runner.center.bmc.core.util.BillingConstants;

public class UnitRuleTest {
	private static Logger logger = Logger.getLogger(UnitRuleTest.class);

	@Test
	public void testBill() throws SchedulerException {
		Map conf = new HashMap<>();
		conf.put(IAccessResourceBook.ACCESS_RESOURCEBOOK, "print");
		conf.put(ICreditControl.CREDIT_CONTROL, "print");
		conf.put(JdbcParam.JDBC_DRIVER, "com.mysql.jdbc.Driver");
		conf.put(JdbcParam.JDBC_URL, "jdbc:mysql://10.1.234.163:3306/ebilling?useUnicode=true&characterEncoding=UTF-8");
		conf.put(JdbcParam.JDBC_USERNAME, "inv");
		conf.put(JdbcParam.JDBC_PASSWORD, "inv");
		JdbcParam jdbcParam = JdbcParam.getInstance(conf);
		ContainerProxy bc = new ContainerProxy(conf, 1000, new AccountReriodRuleManager(StdSchedulerFactory.getDefaultScheduler(),jdbcParam));

		for (int i = 0; i < 10; i++) {
			bc.pushToStatistic(produceTestData(i));
		}
		List<ChargingDetailRecord> details = bc.getContainer().fetchSourceRecords();
//		Assert.assertEquals(10000, details.size());
		System.out.println(details.size());
		Map<GroupFieldValue, AccuFieldValue> map = bc.getContainer().fetchContainerMap();
		System.out.println(map.size());
		for (Entry<GroupFieldValue, AccuFieldValue> entry : map.entrySet()) {
			logger.debug("**************************field**************************");
			for (String field : entry.getKey().getField())
				logger.debug(field);
			logger.debug("**************************value**************************");
			for (Double field : entry.getValue().getDoubleValues())
				logger.debug(field);
		}
	}

	private StreamData produceTestData(int i) {
		List<String> record = new ArrayList<>();
		record.add("CLC");
		record.add("BYD");
		record.add("ELEC");
		record.add("234567890");
		record.add("psn01234");
		record.add("snasdfasdsafdasdfasdf");
		StringBuilder recordBuilder = new StringBuilder();
		recordBuilder.append("cardno12345" + BillingConstants.FIELD_SPLIT);
		recordBuilder.append("cdzhuang001" + BillingConstants.FIELD_SPLIT);
		recordBuilder.append("cdzhan001" + BillingConstants.FIELD_SPLIT);
		recordBuilder.append("20151212130911" + BillingConstants.FIELD_SPLIT);
		recordBuilder.append("20151212140" + i + "11" + BillingConstants.FIELD_SPLIT);
		recordBuilder.append(i  + BillingConstants.FIELD_SPLIT);
		if (i == 0)
			recordBuilder.append(1 + BillingConstants.FIELD_SPLIT);
		else if (i == 9)
			recordBuilder.append(3 + BillingConstants.FIELD_SPLIT);
		else
			recordBuilder.append(2 + BillingConstants.FIELD_SPLIT);
		recordBuilder.append("234567890" + BillingConstants.FIELD_SPLIT);
		recordBuilder.append("12345" + BillingConstants.FIELD_SPLIT);
		
		recordBuilder.append("unit" + BillingConstants.FIELD_SPLIT);
		recordBuilder.append("70" + BillingConstants.FIELD_SPLIT);
		recordBuilder.append("servicenum" + BillingConstants.FIELD_SPLIT);
		recordBuilder.append("acctid" + BillingConstants.FIELD_SPLIT);
		recordBuilder.deleteCharAt(recordBuilder.length() - 1);
		record.add(recordBuilder.toString());
		StreamData streamData = new TestStreamData(record);
		return streamData;
	}
	
	@Test
	public void testjson(){
//		TreeMap<Long, StreamData> hangingRecordMap = new TreeMap<>();
//		hangingRecordMap.put(0L, produceTestData(0));
//		Gson gson = new Gson();
//		System.out.println(gson.toJson(hangingRecordMap.values()));
//		hangingRecordMap.put(1L, produceTestData(1));
//		System.out.println(gson.toJson(hangingRecordMap.values()));
		
		String authAddress = "http://10.1.31.20:19821/iPaas-Auth/service/auth";
		String pid = "2AAE1D1AE3DE4209BB3EC1B1BA3F131C";
		String servicePwd = "111111";
		String serviceId = "MCS002";
		AuthDescriptor ad = new AuthDescriptor(authAddress, pid, servicePwd, serviceId);
		try {
			ICacheClient icache = CacheFactory.getClient(ad);
			icache.set("CLC_BYD_ELEC_6789_record_hanging".getBytes(), "aaaa".getBytes());
//			System.out.println(new String(icache.get("CLC_BYD_ELEC_6789_record_hanging".getBytes())));
		} catch (Exception e) {
			logger.error("error prepare mcs client ........", e);
		}
	}
}
