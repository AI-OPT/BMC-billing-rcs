package com.ai.runner.center.bmc.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Records {

	private String original;
	private Map<String, Integer> indexes;
	private List<Map<String, String>> data = new ArrayList<Map<String, String>>();

	public Records(String original, Map<String, Integer> indexes) {
		this.original = original;
		this.indexes = indexes;
		System.out.println("index="+indexes);
		String[] recordArr = original.split(BillingConstants.RECORD_SPLIT, -1);
		List<String[]> dataList = new ArrayList<>();
		for (int i = 0; i < recordArr.length; i++) {
			dataList.add(recordArr[i].split(BillingConstants.FIELD_SPLIT, -1));
			data.add(new HashMap<String, String>());
		}
		for (Entry<String, Integer> entry : indexes.entrySet()) {
			for (int i = 0; i < dataList.size(); i++) {
				data.get(i).put(entry.getKey(), dataList.get(i)[entry.getValue()]);
			}
		}
	}

	public String getString(int index, String key) {
		return data.get(index).get(key);
	}

	public int getSize() {
		return data.size();
	}

	public List<Map<String, String>> getData() {
		return data;
	}

	public Map<String, String> get(int index) {
		return data.get(index);
	}
	
//	public static void main(String[] args){
//		Gson gson = new Gson();
//		Map<Long,ChargingDetailRecord> map = new TreeMap<>();
//		Map<String,String> map1 = new HashMap<>();
//		map1.put("a1", "a1");
//		map1.put("b1", "b1");
//		ChargingDetailRecord chargingDetailRecord1 = new ChargingDetailRecord();
//		chargingDetailRecord1.setBmcRecordFmtKey(new BmcRecordFmtKey("tena", "busiType"));
//		chargingDetailRecord1.setAccountPeriod("201509");
//		chargingDetailRecord1.putAll(map1);
//		Map<String,String> map2 = new HashMap<>();
//		map2.put("a", "a");
//		map2.put("b", "b");
//		ChargingDetailRecord chargingDetailRecord2 = new ChargingDetailRecord();
//		chargingDetailRecord2.setBmcRecordFmtKey(new BmcRecordFmtKey("tena", "busiType"));
//		chargingDetailRecord2.setAccountPeriod("201509");
//		chargingDetailRecord2.putAll(map2);
//		map.put(1L, chargingDetailRecord1);
//		map.put(2L, chargingDetailRecord2);
//		System.out.println(gson.toJson(map.values()));
//		String gsonStr = gson.toJson(map.values());
//		JsonArray map4 = gson.fromJson(gsonStr, JsonArray.class);
//		System.out.println(map4);
//		System.out.println(map4.size());
//		System.out.println(map4.get(0));
//		System.out.println(map4.get(1));
//		ChargingDetailRecord chargingDetailRecord = (ChargingDetailRecord) gson.fromJson(map4.get(0), ChargingDetailRecord.class);
//		System.out.println(chargingDetailRecord.getAccountPeriod());
//	}
}