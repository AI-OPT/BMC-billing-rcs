package com.ai.runner.center.bmc.core.flow.format;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;

import com.ai.runner.center.bmc.core.biz.api.IDataQuality;
import com.ai.runner.center.bmc.core.dataquality.DataQualityCache;
import com.ai.runner.center.bmc.core.dataquality.DataQualityRtnValue;
import com.ai.runner.center.bmc.core.dataquality.ScriptManager;
import com.ai.runner.center.bmc.core.util.BmcException;
import com.ai.runner.center.bmc.core.util.HBaseProxy;
import com.ai.runner.center.bmc.core.util.RecordUtil;


/**
 * 数据校验通用处理器，提供一种通用的处理方式
 * @author majun
 *
 */
public class GeneralDataQuality implements IDataQuality {
	
	//private DataQualityService dataQualityService=null;
	private Connection conn = HBaseProxy.getConnection();

	/**
	 * 校验重复数据
	 * @param header
	 * @param businessData
	 * @return
	 * @throws BmcException
	 */
	@Override
	public void checkData(Map<String,String> businessData) throws BmcException{
		StringBuilder cacheDupKey = new StringBuilder();
		cacheDupKey.append(businessData.get(RecordUtil.SYSTEM_ID));
		cacheDupKey.append(businessData.get(RecordUtil.TENANT_ID));
		cacheDupKey.append(businessData.get(RecordUtil.SERVICE_ID));
		String dupTableName= assembleDupTable(cacheDupKey.toString(),businessData);
		String dupKey = assembleDuplicateKey(cacheDupKey.toString(),businessData);
		//boolean isSucc = dataQualityService.checkDuplicate(dupTableName,dupKey);
		boolean isSucc = checkDuplicate(dupTableName,dupKey);
		if(!isSucc){
			StringBuilder errorMsg = new StringBuilder();
			errorMsg.append("重复记录[dupKey=").append(dupKey).append("]");
			throw new BmcException("BMC-PRE9000B",errorMsg.toString());
		}
	}

	/**
	 * 格式化数据
	 * @param header
	 * @param businessData
	 * @return
	 * @throws BmcException
	 */
	@Override
	public void formatData(Map<String,String> businessData) throws BmcException{
		StringBuilder cacheDupKey = new StringBuilder();
		cacheDupKey.append(businessData.get(RecordUtil.SYSTEM_ID));
		cacheDupKey.append(businessData.get(RecordUtil.TENANT_ID));
		cacheDupKey.append(businessData.get(RecordUtil.SERVICE_ID));
		Object rtnOjb = ScriptManager.getInstance().executeScript(cacheDupKey.toString(), businessData);
		if(rtnOjb == null){
			return;
		}
		if(!(rtnOjb instanceof DataQualityRtnValue)){
			throw new BmcException("","校验脚本返回结果对象不匹配!");
		}
		DataQualityRtnValue rtnValue = (DataQualityRtnValue)rtnOjb;
		if(!rtnValue.isResult()){
			throw new BmcException(rtnValue.getRtnCode(),rtnValue.getRtnMessage());
		}
	}
	
	
	/**
	 * 组装查重的表名
	 * @param cacheDupKey
	 * @param header
	 * @param businessData
	 * @return
	 * @throws BmcException
	 */
	private String assembleDupTable(String cacheDupKey,Map<String,String> businessData) throws BmcException{
		String suffixKey = DataQualityCache.getInstance().getTbSuffixKey(cacheDupKey);
		String suffixKeyValue = businessData.get(suffixKey);
		System.out.println("suffixKeyValue==========="+suffixKeyValue);
		if(StringUtils.isBlank(suffixKeyValue)){
			throw new BmcException("","查重表中后缀Key对应的Value在实际数据中不存在!");
		}
		suffixKeyValue = filterUnNumber(suffixKeyValue);
		StringBuilder dupTable = new StringBuilder();
		dupTable.append(businessData.get(RecordUtil.SYSTEM_ID)).append("_");
		dupTable.append(businessData.get(RecordUtil.TENANT_ID)).append("_");
		dupTable.append(businessData.get(RecordUtil.SERVICE_ID)).append("_");
		dupTable.append(suffixKeyValue.substring(0, 6));
		return dupTable.toString();
	}
	
	/**
	 * 组装查重的关键字
	 * @param cacheDupKey
	 * @param header
	 * @param businessData
	 * @return
	 */
	private String assembleDuplicateKey(String cacheDupKey,Map<String,String> businessData){
		List<String> keys = DataQualityCache.getInstance().getDupKeyNames(cacheDupKey);
		StringBuilder dupKey = new StringBuilder();
		for(String key:keys){
			dupKey.append(businessData.get(key));
		}
		return dupKey.toString();
	}
	
	private String filterUnNumber(String str){
		String regEx = "[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		return m.replaceAll("").trim();
	}
	
	
	private boolean checkDuplicate(String dupTableName,String dupKey)throws BmcException{
		boolean isSucc = true;
		byte[] rowKey = dupKey.getBytes();
		Table table=null;
		try {
			table = conn.getTable(TableName.valueOf(dupTableName));
			Get get = new Get(rowKey);
			Result result = table.get(get);
			if(result.isEmpty()){
				Put put = new Put(rowKey);
				String create_date = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss");
				put.addColumn("dup".getBytes(), "create_date".getBytes(), create_date.getBytes());
				table.put(put);
			}else{
				isSucc = false;
			}
		} catch (IOException e) {
			throw new BmcException("",e.getMessage());
		} finally{
			if(table != null){
				try {
					table.close();
				} catch (IOException e) {
					throw new BmcException("",e.getMessage());
				}
			}
		}
		return isSucc;
	}
	
}
