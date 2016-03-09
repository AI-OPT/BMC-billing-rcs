package com.ai.runner.center.bmc.core.biz.api;

import java.util.Map;

import com.ai.runner.center.bmc.core.util.BmcException;

/**
 * 数据质量接口，对数据源数据做校验和格式化操作
 * 执行流程：1.initParameter()
 *          2.checkData()
 *          3.formatData()
 * @author majun
 *
 */
public interface IDataQuality {

	/**
	 * 初始化参数
	 * @param metaData
	 * @return
	 */
	//void initParameter(Object metaData);
	
	/**
	 * 查重数据
	 * @param aData
	 * @return
	 */
	void checkData(Map<String,String> businessData) throws BmcException;
	/**
	 * 校验和格式化数据
	 * @param aData
	 * @return
	 */
	void formatData(Map<String,String> businessData) throws BmcException;
	
	/**
	 * 执行前的操作,对于批量只执行一次
	 * @param metaData
	 */
	//void executeBeforeSet(Object metaData);
	
	/**
	 * 执行后的操作,对于批量只执行一次
	 * @param metaData
	 */
	//void executeAfterSet(Object metaData);
}
