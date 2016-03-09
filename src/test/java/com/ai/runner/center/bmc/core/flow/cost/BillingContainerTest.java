package com.ai.runner.center.bmc.core.flow.cost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import com.ai.paas.ipaas.rcs.data.StreamData;
import com.ai.runner.center.bmc.core.flow.cost.container.ContainerProxy;
import com.ai.runner.center.bmc.core.flow.cost.credit.ICreditControl;
import com.ai.runner.center.bmc.core.flow.cost.entity.AccuFieldValue;
import com.ai.runner.center.bmc.core.flow.cost.entity.ChargingDetailRecord;
import com.ai.runner.center.bmc.core.flow.cost.entity.GroupFieldValue;
import com.ai.runner.center.bmc.core.flow.cost.resourcebook.IAccessResourceBook;
import com.ai.runner.center.bmc.core.util.BillingConstants;

public class BillingContainerTest {
	private static Logger logger = Logger.getLogger(BillingContainerTest.class);

	@Test
	public void pushTest() {
		Map conf = new HashMap<>();
		conf.put(IAccessResourceBook.ACCESS_RESOURCEBOOK, "print");
		conf.put(ICreditControl.CREDIT_CONTROL, "print");
		ContainerProxy bc = new ContainerProxy(conf,1000, null);

		// streamData.setTuple(aTuple);
		for (int i = 0; i < 1000; i++) {
			bc.pushToStatistic(produceTestData());
		}
		List<ChargingDetailRecord> details = bc.getContainer().fetchSourceRecords();
		Assert.assertEquals(10000, details.size());
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

	private StreamData produceTestData() {
		Random rand = new Random(10);
		List<String> record = new ArrayList<>();
		record.add("MVNE");
		record.add("GLOV");
		record.add("HTNE");
		record.add("psn01234");
		record.add("20151101130911");
		StringBuilder recordBuilder = new StringBuilder();
		for (int i = 0; i < 10; i++) {
			StringBuilder fieldBuilder = new StringBuilder();
			fieldBuilder.append("15810325361" + BillingConstants.FIELD_SPLIT);
			fieldBuilder.append("15810325360" + BillingConstants.FIELD_SPLIT);
			fieldBuilder.append("20151101130911" + BillingConstants.FIELD_SPLIT);
			fieldBuilder.append("20151101130956" + BillingConstants.FIELD_SPLIT);
			fieldBuilder.append("BJ" + BillingConstants.FIELD_SPLIT);
			fieldBuilder.append(rand.nextDouble() + BillingConstants.FIELD_SPLIT);
			fieldBuilder.append(rand.nextDouble() + BillingConstants.FIELD_SPLIT);
			fieldBuilder.append("gn" + BillingConstants.FIELD_SPLIT);
			fieldBuilder.append("gj" + BillingConstants.FIELD_SPLIT);
			fieldBuilder.append("SUBS00" + rand.nextInt(10) + BillingConstants.FIELD_SPLIT);
			fieldBuilder.append("CUST001" + BillingConstants.FIELD_SPLIT);
			fieldBuilder.append("package");
			recordBuilder.append(fieldBuilder.toString() + BillingConstants.RECORD_SPLIT);
		}
		recordBuilder.deleteCharAt(recordBuilder.length() - 1);
		record.add(recordBuilder.toString());
		StreamData streamData = new TestStreamData(record);
		return streamData;
	}

	@Test
	public void LoadTest() {
		Map conf = new HashMap<>();
		conf.put(IAccessResourceBook.ACCESS_RESOURCEBOOK, "print");
		conf.put(ICreditControl.CREDIT_CONTROL, "print");
		ContainerProxy bc = new ContainerProxy(conf,1000,null);
	}
}
