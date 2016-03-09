package com.ai.runner.center.bmc.core.output;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.log4j.Logger;

import com.ai.runner.center.bmc.core.exception.BillingPrepareException;
import com.ai.runner.center.bmc.core.flow.cost.entity.ChargingDetailRecord;
import com.ai.runner.center.bmc.core.persistence.entity.BmcOutputDetail;
import com.ai.runner.center.bmc.core.persistence.entity.BmcOutputInfo;
import com.ai.runner.center.bmc.core.util.BillingConstants;
import com.ai.runner.center.bmc.core.util.HbaseOutputInfos;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 向hbase输出
 * 
 * @author bixy
 *
 */
public class HbaseOutputProcessor implements IOutputProcessor {
	private static Logger logger = Logger.getLogger(HbaseOutputProcessor.class);
	private Connection connection;
	private HbaseOutputInfos hbaseOutputInfos;

	public HbaseOutputProcessor(HbaseOutputInfos hbaseOutputInfos, Map<String, String> config) {
		this.hbaseOutputInfos = hbaseOutputInfos;
		Configuration configuration = HBaseConfiguration.create();
		JsonParser jsonParser = new JsonParser();
		JsonObject jsonObject = (JsonObject) jsonParser.parse(config.get(BillingConstants.BILLING_HBASE_PARAM));
		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			configuration.set(entry.getKey(), entry.getValue().getAsString());
		}
		try {
			this.connection = ConnectionFactory.createConnection(configuration);
		} catch (IOException e) {
			// 这里需要中断整个程序的启动
			throw new BillingPrepareException(e);
		}
	}

	@Override
	public void execute(List<ChargingDetailRecord> detailRecords) {
		// hbaseService.batchInsert(detailRecords);
		Map<String, List<Put>> batchPut = new HashMap<>();
		try {
			for (ChargingDetailRecord chargingDetailRecord : detailRecords) {
				List<BmcOutputInfo> outputInfos = hbaseOutputInfos.getOutputMappingValue(chargingDetailRecord.getBmcRecordFmtKey());
				if (outputInfos != null)
					for (BmcOutputInfo outputInfo : outputInfos) {
						String tableName = outputInfo.getTableName() + chargingDetailRecord.getAccountPeriod();
						List<Put> puts = batchPut.get(tableName);
						if (puts == null) {
							puts = new ArrayList<>();
							batchPut.put(tableName, puts);
						}
						Put put = new Put(outputInfo.getRowKey(chargingDetailRecord).getBytes());
						for (BmcOutputDetail cpOutputDetail : outputInfo.getDetails()) {
							byte[] colName = cpOutputDetail.getColumnName().getBytes();
							String value = chargingDetailRecord.get(cpOutputDetail.getParamName());
							put.addColumn(colName, colName, (value == null ? "" : value).getBytes());

						}
						puts.add(put);
					}
			}
			for (Entry<String, List<Put>> entry : batchPut.entrySet()) {
				Table table = connection.getTable(TableName.valueOf(entry.getKey()));
				table.batch(entry.getValue(), new Object[entry.getValue().size()]);
				table.close();
			}
		} catch (IOException e) {
			logger.error(e);
		} catch (InterruptedException e) {
			logger.error(e);
		}
	}
}
