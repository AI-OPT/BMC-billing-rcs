package com.ai.runner.center.bmc.business.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.runner.center.bmc.core.util.BmcException;
import com.ai.runner.center.bmc.core.util.CacheClient;

/**
 * 封装对资费业务操作，数据从共享内存中读取
 * @author majun
 *
 */
public class PriceInfo {
	private static Logger logger = LoggerFactory.getLogger(PriceInfo.class);
	private CacheClient cacheClient = CacheClient.getInstance();
	
	/**
	 * 查询用户信息，并设置到结果集
	 * @param data
	 * @throws Exception
	 */
//	public void setUserData(Map<String, String> data) throws BmcException{
//		Map<String, String> params = new HashMap<String, String>();
//		params.put("service_num", data.get("service_num"));
//		List<Map<String, String>> result = cacheClient.doQuery("subs_user", params);
//		if(result == null || result.size()==0){
//			throw new BmcException("BMC-MATCH0001B","subs_user表没有找到用户信息!");
//		}
//		String subs_id = result.get(0).get("subs_id");
//		String cust_id = result.get(0).get("cust_id");
//		params = new HashMap<String, String>();
//		params.put("subs_id", subs_id);
//		result = cacheClient.doQuery("subs_comm", params);
//		if(result == null || result.size()==0){
//			throw new BmcException("BMC-MATCH0002B","subs_comm表没有找到用户信息!");
//		}
//		String component_id = result.get(0).get("component_id");
//		logger.debug("[component_id]---->>>"+component_id);
//		
//		data.put("component_id", component_id);
//		data.put("cust_id", cust_id);
//	}
	
	/**
	 * 查询资费明细表，并设置结果集
	 * @param data
	 * @throws Exception
	 */
	public void setPriceDetailData(Map<String, String> data) throws BmcException{
		Map<String, String> params = new HashMap<String, String>();
		params.put("price_code", data.get("component_id"));
		List<Map<String, String>> result = cacheClient.doQuery("cp_price_detail", params);
		if(result == null || result.size()==0){
			throw new BmcException("BMC-MATCH0003B","cp_price_detail表没有找到资费明细信息!");
		}
		Map<String, String> tempData = result.get(0);
		///////////需要根据有效期判断此业务是否过期了////////////
		
		data.put("cal_type", tempData.get("charge_type"));
		data.put("detail_code", tempData.get("detail_code"));
		data.put("class", tempData.get("class"));
	}
	
	/**
	 * 查询单价列表，并设置结果集
	 * @param data
	 * @throws Exception
	 */
	public void setUnitpriceInfoData(Map<String, String> data) throws BmcException{
		Map<String, String> params = new HashMap<String, String>();
		params.put("unitprice_code", data.get("detail_code"));
		List<Map<String, String>> result = cacheClient.doQuery("cp_unitprice_info", params);
		if(result == null || result.size()==0){
			throw new BmcException("","cp_unitprice_info表没有找到单价信息!");
		}
		Map<String, String> tempData = result.get(0);
		data.put("factor_code", tempData.get("factor_code"));
		data.put("fee_item_code", tempData.get("fee_item_code"));
		
		logger.debug("[fee_item_code]---->>>"+tempData.get("fee_item_code"));
	}
	
	/**
	 * 查询单价费用项，并设置结果集
	 * @param data
	 * @throws Exception
	 */
	public void setUnitpriceItemData(Map<String, String> data) throws BmcException{
		logger.debug("[setUnitpriceItemData|fee_item_code]---->>>"+data.get("fee_item_code"));
		Map<String, String> params = new HashMap<String, String>();
		params.put("unitprice_code", data.get("fee_item_code"));
		List<Map<String, String>> result = cacheClient.doQuery("cp_unitprice_item", params);
		if(result == null || result.size()==0){
			throw new BmcException("","cp_unitprice_item表没有找到单价费用项信息!");
		}
		Map<String, String> tempData = result.get(0);
		data.put("price_value", tempData.get("price_value"));
		data.put("unit_type", tempData.get("unit_type"));
		data.put("subject_code", tempData.get("subject_code"));
	}
	
	
	/**
	 * 校验参考因素，从共享内存中取出参考因素和协议中数值比对
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public boolean matchingFactor(Map<String, String> data) throws BmcException{
		boolean isMatching = false;
		String[] factorCodes = StringUtils.splitPreserveAllTokens(data.get("factor_code"), CacheClient.delimiter);
		String[] feeItemCodes = StringUtils.splitPreserveAllTokens(data.get("fee_item_code"), CacheClient.delimiter);
		int len = factorCodes.length;
		for(int i=0;i<len;i++){
			//factorMappping.put(StringUtils.trim(factorCodes[i]), StringUtils.trim(feeItemCodes[i]));
			String code = StringUtils.trim(factorCodes[i]);
			isMatching = verifyFactor(code, data);
			if(isMatching){
				data.put("factor_code", code);
				data.put("fee_item_code", feeItemCodes[i]);
				break;
			}
		}
		return isMatching;
	}
	
	public boolean verifyFactor(String factorCode, Map<String, String> data){
		Map<String, String> params = new HashMap<String, String>();
		params.put("factor_code", factorCode);
		List<Map<String, String>> result = null;
		try {
			result = cacheClient.doQuery("cp_factor_info", params);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(result == null || result.size()==0){
			logger.debug("factor_code:"+factorCode+",在cp_factor_info表没有找到参考因素信息!");
			return false;
		}
		Map<String, String> tempData = result.get(0);
		Map<String,String> factorMap = splitFactor(tempData);
		Entry<String,String> entry;
		//共享内存中参考因素key
		String factorKey="";
		//共享内存中参考因素value,协议中参考因素value
		String factorValue="",dataValue="";
		for(Iterator<Entry<String,String>> iter=factorMap.entrySet().iterator();iter.hasNext();){
			entry = iter.next();
			factorKey = entry.getKey();
			factorValue = entry.getValue();
			dataValue = data.get(factorKey);
			//协议中参考因素不存在
			if(StringUtils.isBlank(dataValue)){
				return false;
			}
			//判断共享内存和协议中的参考因素值是否匹配
			if(!factorValue.equals(dataValue)){
				return false;
			}
		}
		return true;
	}
	
	
	
	
	/**
	 * 将参考因素内容进行分割成多行
	 * @param paramData
	 * @param delimiter
	 * @return
	 */
	private Map<String,String> splitFactor(Map<String,String> paramData){
		Map<String, String> data = new HashMap<String, String>();
		String factorName = paramData.get("factor_name");
		String[] fnArray = StringUtils.splitPreserveAllTokens(factorName, CacheClient.delimiter);
		String factorValue = paramData.get("factor_value");
		String[] fvArray = StringUtils.splitPreserveAllTokens(factorValue, CacheClient.delimiter);
		for(int i=0;i<fnArray.length;i++){
			data.put(fnArray[i], fvArray[i]);
		}
		logger.debug("[splitFactor]---->>>"+data.toString());
		return data;
	}
	
	
	
	
	
}
