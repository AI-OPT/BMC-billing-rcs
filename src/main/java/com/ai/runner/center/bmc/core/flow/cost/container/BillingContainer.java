package com.ai.runner.center.bmc.core.flow.cost.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.ai.runner.center.bmc.core.flow.cost.container.strategy.TimeFlushStrategy;
import com.ai.runner.center.bmc.core.flow.cost.entity.AccuFieldValue;
import com.ai.runner.center.bmc.core.flow.cost.entity.AccuRule;
import com.ai.runner.center.bmc.core.flow.cost.entity.ChargingDetailRecord;
import com.ai.runner.center.bmc.core.flow.cost.entity.GroupFieldValue;
import com.ai.runner.center.bmc.core.flow.cost.entity.SubjectAndPriceValue;
import com.ai.runner.center.bmc.core.persistence.entity.BmcRecordFmt.BmcRecordFmtKey;
import com.ai.runner.center.bmc.core.util.BillingConstants;

/**
 * 累账容器
 * 
 * @author bixy
 * 
 */
public class BillingContainer extends StatisticContainer<AccuFieldValue, ChargingDetailRecord> {
	private static Logger logger = Logger.getLogger(BillingContainer.class);
	public static final String FLUSH_METRIC = "billing.flush.time.metric";
	private Map<BmcRecordFmtKey, List<AccuRule>> accuRuleMap = new HashMap<>(); // 累计规则
	private List<ChargingDetailRecord> sourceRecords = new ArrayList<>(); // 存储计算后的源数据，可入hbase

	public BillingContainer(Map<BmcRecordFmtKey, List<AccuRule>> accuRuleMap, int timeMetric) {
		this.accuRuleMap = accuRuleMap;
		super.flushStrategy = new TimeFlushStrategy(timeMetric);
	}

	@Override
	public void pushToStatistic(ChargingDetailRecord chargingDetailRecord) {
		sourceRecords.add(chargingDetailRecord);
		// 在计费规则中，则放入容器累计
		if (accuRuleMap.containsKey(chargingDetailRecord.getBmcRecordFmtKey()) && chargingDetailRecord.getFees() != null) {
			for (AccuRule accuRule : accuRuleMap.get(chargingDetailRecord.getBmcRecordFmtKey())) {
				for (Entry<Integer, SubjectAndPriceValue> entry : chargingDetailRecord.getFees().entrySet()) {
					// 将费用信息加入详单，subject_id和total为了累费使用，subject和fee用于输出详单
					chargingDetailRecord.put(BillingConstants.SUBJECT_ID, entry.getValue().getSubjectCode());
					chargingDetailRecord.put(BillingConstants.TOTAL, entry.getValue().getPriceValue() + "");
					chargingDetailRecord.put(BillingConstants.SUBJECT + entry.getKey(), entry.getValue().getSubjectCode());
					chargingDetailRecord.put(BillingConstants.FEE + entry.getKey(), entry.getValue().getPriceValue() + "");
					
					GroupFieldValue groupFieldValue = accuRule.getGroupFieldValue(chargingDetailRecord);
					AccuFieldValue accuFieldValue = super.container.get(groupFieldValue);
					if (accuFieldValue == null) {
						accuFieldValue = accuRule.getAccuRuleValue(chargingDetailRecord.getFields());
						super.container.put(groupFieldValue, accuFieldValue);
					} else {
						accuFieldValue.add(accuRule.getAccuRuleValue(chargingDetailRecord.getFields()));
					}
				}
				// 累费使用的subject_id和total,使用完成后可以删掉
				chargingDetailRecord.remove(BillingConstants.SUBJECT_ID);
				chargingDetailRecord.remove(BillingConstants.TOTAL);
			}
		}
	}

	public List<ChargingDetailRecord> fetchSourceRecords() {
		logger.debug("fetch source records " + sourceRecords.size());
		List<ChargingDetailRecord> result = sourceRecords;
		sourceRecords = new ArrayList<>();
		return result;
	}

}
