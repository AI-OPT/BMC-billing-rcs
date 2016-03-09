package com.ai.runner.center.bmc.core.flow.cost.rule.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.ai.paas.ipaas.mcs.CacheFactory;
import com.ai.paas.ipaas.mcs.interfaces.ICacheClient;
import com.ai.paas.ipaas.uac.vo.AuthDescriptor;
import com.ai.runner.center.bmc.core.entity.JdbcParam;
import com.ai.runner.center.bmc.core.exception.BillingPrepareException;
import com.ai.runner.center.bmc.core.flow.cost.CostFactory;
import com.ai.runner.center.bmc.core.flow.cost.credit.ICreditControl;
import com.ai.runner.center.bmc.core.flow.cost.entity.ChargingDetailRecord;
import com.ai.runner.center.bmc.core.flow.cost.entity.FeeTypeAndSubject;
import com.ai.runner.center.bmc.core.flow.cost.entity.FeeTypeInfo;
import com.ai.runner.center.bmc.core.flow.cost.entity.PaasParam;
import com.ai.runner.center.bmc.core.flow.cost.entity.SubjectAndPrice;
import com.ai.runner.center.bmc.core.flow.cost.entity.SubjectAndPriceValue;
import com.ai.runner.center.bmc.core.flow.cost.entity.UnitMCSParam;
import com.ai.runner.center.bmc.core.flow.cost.resourcebook.IAccessResourceBook;
import com.ai.runner.center.bmc.core.flow.cost.rule.AbstractBilling;
import com.ai.runner.center.bmc.core.flow.cost.rule.IBilling;
import com.ai.runner.center.bmc.core.flow.cost.rule.IRateFinder;
import com.ai.runner.center.bmc.core.flow.cost.rule.rate.impl.IntervalRateFinder.Interval;
import com.ai.runner.center.bmc.core.flow.cost.rule.rate.impl.TimeUnitRateFinder;
import com.ai.runner.center.bmc.core.persistence.entity.CpExtInfo;
import com.ai.runner.center.bmc.core.persistence.entity.CpUnitpriceItem;
import com.ai.runner.center.bmc.core.util.BillingConstants;
import com.ai.runner.center.bmc.core.util.JdbcTemplate;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * 单价计费规则
 * 
 * @author bixy
 *
 */
public class Unit extends AbstractBilling implements IBilling {
	private static Logger logger = Logger.getLogger(Unit.class);
	public static final String BILLING_UNIT_MCS_PARAM = "billing.unit.mcs.param";
	public static final String BILLING_UNIT_CAL_TIMEOUT = "billing.unit.cal.timeout";
	private static final String CP_UNITPRICE_ITEM_SELECT = "select a.ID id,a.FEE_ITEM_CODE feeItemCode,a.FEE_TYPE feeType,a.PRICE_VALUE priceValue,a.UNIT_TYPE unitType,a.SUBJECT_CODE subjectCode,a.ITEM_EXT_CODE itemExtCode from cp_unitprice_item a";
	private static final String CP_EXT_INFO_SELECT = "select a.EXT_CODE extCode,a.EXT_VALUE extValue,a.EXT_NAME extName from cp_ext_info a where a.EXT_CODE = ?";
	private int timeout = 60 * 60 * 16;
	private Gson jsonConverter = new Gson();
	private ICacheClient icache;
	private ICreditControl creditControl;
	private IAccessResourceBook accessResourceBook;
	private Map<String, FeeTypeInfo> rateMap = new HashMap<>();

	public Unit(Map conf) {
		creditControl = CostFactory.getCreditControl((String) conf.get(ICreditControl.CREDIT_CONTROL), (String) conf.get(ICreditControl.CREDIT_CONTROL_PARAM));
		accessResourceBook = CostFactory.getAccessResourceBook((String) conf.get(IAccessResourceBook.ACCESS_RESOURCEBOOK), (String) conf.get(IAccessResourceBook.ACCESS_RESOURCEBOOK_PARAM));
		prepareMcs(conf);
		loadRate(conf);
		String timeOutString = (String) conf.get(BILLING_UNIT_CAL_TIMEOUT);
		if (StringUtils.isNotEmpty(timeOutString)) {
			timeout = Integer.parseInt(timeOutString);
		} else {
			logger.warn("billing.unit.cal.timeout isn't be set properly ,use default 16 hours");
		}
	}

	@Override
	public Map<FeeTypeAndSubject, IRateFinder> getRate(ChargingDetailRecord record) {
		FeeTypeInfo feeTypeInfo = rateMap.get(record.get(BillingConstants.FEE_ITEM_CODE));
		if (feeTypeInfo != null) {
			// 离线记录,没有结束时间，获取最小费率
			if (StringUtils.isEmpty(record.get(BillingConstants.END_TIME)))
				return feeTypeInfo.getMinRate();
			else
				return feeTypeInfo.getRate(record);
		} else {
			logger.error("couldn't find feeTypeInfo for item code " + record.get(BillingConstants.FEE_ITEM_CODE));
			return null;
		}
	}

	@Override
	public List<ChargingDetailRecord> calculate(Map<FeeTypeAndSubject, IRateFinder> rateInfo, ChargingDetailRecord chargingDetailRecord) {
		if (MapUtils.isEmpty(rateInfo)) {
			// 如果不含批价信息，将记录写错单表
			logger.error("couldn't find rate info for record,sn is ..........." + chargingDetailRecord.get(BillingConstants.SN));
			// 返回空集合
			return new ArrayList<>();
		}
		List<ChargingDetailRecord> result = new ArrayList<>();
		// 从redis获取上次流量单费用和流量
		String key = chargingDetailRecord.getBmcRecordFmtKey().toJoinString() + BillingConstants.COMMON_JOINER + chargingDetailRecord.get(BillingConstants.SUBS_ID) + BillingConstants.COMMON_JOINER
				+ chargingDetailRecord.get(BillingConstants.START_TIME);
		String value = icache.get(key);
		String endTime = chargingDetailRecord.get(BillingConstants.END_TIME);
		// 离线记录,没有结束时间，走特殊处理
		if (StringUtils.isEmpty(endTime)) {
			UnitMCSParam unitMCSParam = null;
			if (StringUtils.isNotEmpty(value))
				unitMCSParam = new UnitMCSParam(value);
			else
				unitMCSParam = new UnitMCSParam(new BigDecimal("0"), new BigDecimal("0"), "", "00000000000000");
			offlineCalculate(rateInfo, key, unitMCSParam, chargingDetailRecord);
			result.add(chargingDetailRecord);
			return result;
		}
		if (StringUtils.isNotEmpty(value)) {
			UnitMCSParam unitMCSParam = new UnitMCSParam(value);
			if (endTime.compareTo(unitMCSParam.getCurrentPoint()) <= 0 || "offline".equals(unitMCSParam.getCurrentPoint())) {
				// 结束时间小于已经被计算过的记录，则直接返回，不被计算
				// 结束时间被设置成offline，说明离网数据已经被计算，后来数据被丢弃
				result.add(chargingDetailRecord);
				return result;
			} else {
				// 顺序情况
				normalCalculate(rateInfo, key, result, unitMCSParam, chargingDetailRecord, getHangingRecord(key));
			}
		} else {
			if (!canBeFirstRecord(rateInfo, chargingDetailRecord)) {
				// 如果来的不是电量等于0的记录,说明不是起始记录，则被挂起
				logger.info("offline hangRecord because this record can not be first record!" + chargingDetailRecord.get(BillingConstants.SN));
				hangRecord(key, chargingDetailRecord);
			} else {
				// 首条记录
				UnitMCSParam unitMCSParam = new UnitMCSParam(new BigDecimal("0"), new BigDecimal("0"), "", "00000000000000");
				normalCalculate(rateInfo, key, result, unitMCSParam, chargingDetailRecord, getHangingRecord(key));
			}
		}
		return result;
	}

	private boolean canBeFirstRecord(Map<FeeTypeAndSubject, IRateFinder> rateInfo, ChargingDetailRecord chargingDetailRecord) {
		for (Entry<FeeTypeAndSubject, IRateFinder> entry : rateInfo.entrySet()) {
			if (entry.getValue() instanceof TimeUnitRateFinder) {
				TimeUnitRateFinder timeUnitRateFinder = (TimeUnitRateFinder) entry.getValue();
				// 如果起始时间是一个区间的起始点
				String start = timeUnitRateFinder.getFormatTime(chargingDetailRecord, BillingConstants.START_TIME);
				String end = timeUnitRateFinder.getFormatTime(chargingDetailRecord, BillingConstants.END_TIME);
				if (timeUnitRateFinder.isKeyPoint(start)) {
					// 判断，结束时间是否在同一个区间内
					Interval interval = timeUnitRateFinder.getIntervalByStart(start);
					return interval.isInInterval(end);
				} else {
					String intervalStart = timeUnitRateFinder.getIntervalStart(start);
					Interval interval = timeUnitRateFinder.getIntervalByStart(intervalStart);
					return interval.isInInterval(end);
				}
			}
		}
		return true;
	}

	/**
	 * 构建连接mcs服务
	 */
	private void prepareMcs(Map conf) {
		PaasParam paasParam = (new Gson()).fromJson((String) conf.get(BILLING_UNIT_MCS_PARAM), PaasParam.class);
		AuthDescriptor ad = new AuthDescriptor(paasParam.getAuthAddr(), paasParam.getpId(), paasParam.getPassword(), paasParam.getSrvId());
		try {
			icache = CacheFactory.getClient(ad);
		} catch (Exception e) {
			throw new BillingPrepareException(e);
		}
	}

	/**
	 * 加载批价信息
	 */
	private void loadRate(Map conf) {
		Connection connection = null;
		try {
			JdbcParam jdbcParam = JdbcParam.getInstance(conf);
			connection = JdbcTemplate.getConnection(jdbcParam);

			List<CpUnitpriceItem> list = JdbcTemplate.query(CP_UNITPRICE_ITEM_SELECT, connection, new BeanListHandler<CpUnitpriceItem>(CpUnitpriceItem.class));
			for (CpUnitpriceItem cpUnitpriceItem : list) {
				FeeTypeInfo feeTypeInfo = rateMap.get(cpUnitpriceItem.getFeeItemCode());
				if (feeTypeInfo == null) {
					feeTypeInfo = new FeeTypeInfo();
					rateMap.put(cpUnitpriceItem.getFeeItemCode(), feeTypeInfo);
				}
				String itemExtCode = cpUnitpriceItem.getItemExtCode();
				List<CpExtInfo> cpExtInfos = JdbcTemplate.query(CP_EXT_INFO_SELECT, connection, new BeanListHandler<CpExtInfo>(CpExtInfo.class), itemExtCode);
				feeTypeInfo.putFeeTypeInfo(cpUnitpriceItem.getFeeType(), cpUnitpriceItem.getSubjectCode(), cpUnitpriceItem.getPriceValueBigDecimal(), cpExtInfos);
			}
		} catch (ClassNotFoundException e) {
			throw new BillingPrepareException(e);
		} catch (SQLException e) {
			throw new BillingPrepareException(e);
		} finally {
			DbUtils.closeQuietly(connection);
		}
	}

	/**
	 * 生成信控发送数据
	 * 
	 * @param fields
	 * @return
	 */
	private String getCreditSendData(Map<String, String> fields, BigDecimal fee) {
		Map<String, String> map = new HashMap<>();
		map.put(BillingConstants.EVENT_ID, fields.get(BillingConstants.SN));
		map.put(BillingConstants.SYSTEM_ID, fields.get(BillingConstants.SYSTEM_ID));
		map.put(BillingConstants.TENANT_ID, fields.get(BillingConstants.TENANT_ID));
		map.put(BillingConstants.SOURCE_TYPE, BillingConstants.SOURCE_TYPE_VALUE);
		map.put(BillingConstants.OWNER_TYPE, BillingConstants.OWNER_TYPE_ACCT);
		map.put(BillingConstants.OWNER_ID, fields.get(BillingConstants.ACCT_ID));
		map.put(BillingConstants.EVENT_TYPE, BillingConstants.EVENT_TYPE_MAIN);
		map.put(BillingConstants.AMOUNT, fee.doubleValue() + "");
		map.put(BillingConstants.AMOUNT_MARK, BillingConstants.AMOUNT_MARK_MINUS);
		map.put(BillingConstants.AMOUNT_TYPE, BillingConstants.AMOUNT_TYPE_BOOK);
		JsonObject expand = new JsonObject();
		expand.addProperty(BillingConstants.CHARGING_PILE, fields.get(BillingConstants.CHARGING_PILE));
		expand.addProperty(BillingConstants.CHARGING_STATION, fields.get(BillingConstants.CHARGING_STATION));
		map.put(BillingConstants.EXPANDED_INFO, jsonConverter.toJson(expand));
		return jsonConverter.toJson(map);
	}

	/**
	 * 生成资源扣减数据
	 * 
	 * @return
	 */
	private String getReduceSendData(Map<String, String> fields, BigDecimal fee) {
		Map<String, String> map = new HashMap<>();
		map.put(BillingConstants.EVENT_ID, fields.get(BillingConstants.SN));
		map.put(BillingConstants.SYSTEM_ID, fields.get(BillingConstants.SYSTEM_ID));
		map.put(BillingConstants.TENANT_ID, fields.get(BillingConstants.TENANT_ID));
		map.put(BillingConstants.ACCT_ID, fields.get(BillingConstants.ACCT_ID));
		map.put(BillingConstants.SUBS_ID, fields.get(BillingConstants.SUBS_ID));
		map.put(BillingConstants.AMOUNT, fee.doubleValue() + "");
		map.put(BillingConstants.AMOUNT_TYPE, BillingConstants.AMOUNT_TYPE_BOOK);
		return jsonConverter.toJson(map);
	}

	/**
	 * 没给结束时间的特殊情况，准离线记录
	 * 
	 * @param rateInfo
	 * @param key
	 * @param unitMCSParam
	 * @param chargingDetailRecord
	 */
	private void offlineCalculate(Map<FeeTypeAndSubject, IRateFinder> rateInfo, String key, UnitMCSParam unitMCSParam, ChargingDetailRecord chargingDetailRecord) {
		Map<Integer, SubjectAndPriceValue> map = new HashMap<>();
		BigDecimal consumption = new BigDecimal(chargingDetailRecord.get(BillingConstants.CONSUMPTION));
		BigDecimal recordFee = new BigDecimal("0");
		for (Entry<FeeTypeAndSubject, IRateFinder> entry : rateInfo.entrySet()) {
			FeeTypeAndSubject feeTypeAndSubject = entry.getKey();
			IRateFinder rateFinder = entry.getValue();
			SubjectAndPrice subjectAndPrice = new SubjectAndPrice(feeTypeAndSubject.getSubject(), rateFinder.getCheapestRate());
			SubjectAndPriceValue subjectAndPriceValue = new SubjectAndPriceValue(subjectAndPrice, consumption.subtract(unitMCSParam.getPowerConsumption()));
			map.put(feeTypeAndSubject.getFeeType(), subjectAndPriceValue);
			recordFee = recordFee.add(subjectAndPriceValue.getPriceValue());
		}
		BigDecimal fee = unitMCSParam.getFee().add(recordFee);
		chargingDetailRecord.setFees(map);

		// 删除缓存数据
		unitMCSParam = new UnitMCSParam(consumption, fee, "", "offline");
		icache.setex(key, timeout, unitMCSParam.value());
		icache.del(key + BillingConstants.COMMON_JOINER + BillingConstants.RECORD_HANGING);

		creditControl.send(getCreditSendData(chargingDetailRecord.getFields(), fee), chargingDetailRecord);
		accessResourceBook.reduce(getReduceSendData(chargingDetailRecord.getFields(), recordFee), chargingDetailRecord);
	}

	/**
	 * 正常计算
	 * 
	 * @param key
	 * @param result
	 * @param unitMCSParam
	 * @param chargingDetailRecord
	 * @param hangingRecordMap
	 */
	private void normalCalculate(Map<FeeTypeAndSubject, IRateFinder> rateInfo, String key, List<ChargingDetailRecord> result, UnitMCSParam unitMCSParam, ChargingDetailRecord chargingDetailRecord,
			TreeMap<Long, ChargingDetailRecord> hangingRecordMap) {
		Map<Integer, SubjectAndPriceValue> map = new HashMap<>();
		BigDecimal consumption = new BigDecimal(chargingDetailRecord.get(BillingConstants.CONSUMPTION));
		BigDecimal recordFee = new BigDecimal("0");
		String keyPoint = unitMCSParam.getKeyPoint();
		for (Entry<FeeTypeAndSubject, IRateFinder> entry : rateInfo.entrySet()) {
			if (entry.getValue() instanceof TimeUnitRateFinder) {
				TimeUnitRateFinder timeUnitRateFinder = (TimeUnitRateFinder) entry.getValue();
				if (StringUtils.isEmpty(unitMCSParam.getKeyPoint())) {
					String tempKeyPoint = timeUnitRateFinder.getIntervalStart(chargingDetailRecord);
					if (StringUtils.isEmpty(keyPoint))
						keyPoint = tempKeyPoint;
					else if (tempKeyPoint.compareTo(keyPoint) > 0) {
						keyPoint = tempKeyPoint;
					}
				}
				// 如果不在一个时间范围内，则挂起记录，首先比较缓存里的关键点是否与当前时间所处同一个费率区间
				// 如果不同，还要判断当前时间是否为下一个区间的起始点，如果不是则挂起，如果是，就需要比较
				else if (!timeUnitRateFinder.getIntervalStart(unitMCSParam.getKeyPoint()).equals(timeUnitRateFinder.getIntervalStart(chargingDetailRecord))) {
					// 如果不同，且当前时间还不是起始点，说明一定是不能计算的记录，则挂起
					// 如果不同，且当前时间是起始点，则需要判断，本起始点的上个起始点与关键时间点所属起始点是否相同，不同，则挂起
					if (!timeUnitRateFinder.isKeyPoint(chargingDetailRecord)
							|| !timeUnitRateFinder.getIntervalStart(unitMCSParam.getKeyPoint()).equals(timeUnitRateFinder.getPreIntervalStart(chargingDetailRecord))) {
						hangingRecordMap.put(Long.parseLong(chargingDetailRecord.get(BillingConstants.END_TIME)), chargingDetailRecord);
						logger.info("hang record because over keypoint " + chargingDetailRecord.get(BillingConstants.SN));
						hangRecord(key, hangingRecordMap);
						// 是否还有必要请求一次关键时间点数据????
						return;
					}
				}

				if (timeUnitRateFinder.isKeyPoint(chargingDetailRecord)) {
					String tempKeyPoint = timeUnitRateFinder.getIntervalStart(chargingDetailRecord);
					if (StringUtils.isEmpty(keyPoint))
						keyPoint = tempKeyPoint;
					else if (tempKeyPoint.compareTo(keyPoint) > 0) {
						keyPoint = tempKeyPoint;
					}
				}
			}
			FeeTypeAndSubject feeTypeAndSubject = entry.getKey();
			IRateFinder rateFinder = entry.getValue();
			SubjectAndPrice subjectAndPrice = new SubjectAndPrice(feeTypeAndSubject.getSubject(), rateFinder.getRate(chargingDetailRecord));
			SubjectAndPriceValue subjectAndPriceValue = new SubjectAndPriceValue(subjectAndPrice, consumption.subtract(unitMCSParam.getPowerConsumption()));
			map.put(feeTypeAndSubject.getFeeType(), subjectAndPriceValue);
			recordFee = recordFee.add(subjectAndPriceValue.getPriceValue());
		}
		BigDecimal fee = unitMCSParam.getFee().add(recordFee);
		chargingDetailRecord.setFees(map);
		// if(!keyPoint.equals(unitMCSParam.getKeyPoint())){
		logger.info("get keyPoint change before is " + unitMCSParam.getKeyPoint() + ",after is " + keyPoint);
		logger.info("sum fee is ........." + fee.doubleValue());
		logger.info("consumption is .............." + consumption.doubleValue());
		// }

		unitMCSParam = new UnitMCSParam(consumption, fee, keyPoint, chargingDetailRecord.get(BillingConstants.END_TIME));
		icache.setex(key, timeout, unitMCSParam.value());
		result.add(chargingDetailRecord);

		// 发送信控，结束不发信控
		creditControl.send(getCreditSendData(chargingDetailRecord.getFields(), fee), chargingDetailRecord);
		accessResourceBook.reduce(getReduceSendData(chargingDetailRecord.getFields(), recordFee), chargingDetailRecord);

		if (MapUtils.isNotEmpty(hangingRecordMap)) {
			Entry<Long, ChargingDetailRecord> entry = hangingRecordMap.pollFirstEntry();
			normalCalculate(rateInfo, key, result, unitMCSParam, entry.getValue(), hangingRecordMap);
		} else {
			icache.del(key + BillingConstants.COMMON_JOINER + BillingConstants.RECORD_HANGING);
		}

	}

	/**
	 * 挂起不能被处理的数据
	 * 
	 * @param key
	 * @param chargingDetailRecord
	 */
	private void hangRecord(String key, ChargingDetailRecord chargingDetailRecord) {
		TreeMap<Long, ChargingDetailRecord> hangingRecordMap = getHangingRecord(key);
		hangingRecordMap.put(Long.parseLong(chargingDetailRecord.get(BillingConstants.END_TIME)), chargingDetailRecord);
		Gson gson = new Gson();
		String hangingValue = gson.toJson(hangingRecordMap.values());
		logger.info("hanging hangingRecordMap.........." + hangingValue);
		String hangingKey = key + BillingConstants.COMMON_JOINER + BillingConstants.RECORD_HANGING;
		icache.setex(hangingKey, timeout, hangingValue);
	}

	/**
	 * 挂起不能被处理的数据
	 * 
	 * @param key
	 * @param hangingRecordMap
	 */
	private void hangRecord(String key, TreeMap<Long, ChargingDetailRecord> hangingRecordMap) {
		String hangingKey = key + BillingConstants.COMMON_JOINER + BillingConstants.RECORD_HANGING;
		String hangingValue = (new Gson()).toJson(hangingRecordMap.values());
		logger.info("hanging hangingRecordMap.........." + hangingValue);
		icache.setex(hangingKey, timeout, hangingValue);
	}

	/**
	 * 获取被挂起的数据
	 * 
	 * @param key
	 * @return
	 */
	private TreeMap<Long, ChargingDetailRecord> getHangingRecord(String key) {
		String hangingKey = key + BillingConstants.COMMON_JOINER + BillingConstants.RECORD_HANGING;
		String hangingValue = icache.get(hangingKey);
		Gson gson = new Gson();
		TreeMap<Long, ChargingDetailRecord> hangingRecordMap = new TreeMap<>();
		if (StringUtils.isNotEmpty(hangingValue)) {
			System.out.println("hangingKey===================" + hangingKey + "==============");
			System.out.println("hangingValue===================" + hangingValue);
			JsonArray hangingRecordArr = gson.fromJson(hangingValue, JsonArray.class);
			for (int i = 0; i < hangingRecordArr.size(); i++) {
				ChargingDetailRecord hangingRecord = (ChargingDetailRecord) gson.fromJson(hangingRecordArr.get(i), ChargingDetailRecord.class);
				hangingRecordMap.put(Long.parseLong(hangingRecord.get(BillingConstants.END_TIME)), hangingRecord);
			}
		}
		return hangingRecordMap;
	}

	public Map<String, FeeTypeInfo> getRateMap() {
		return rateMap;
	}

}
