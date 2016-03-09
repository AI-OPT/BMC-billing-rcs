package com.ai.runner.center.bmc.core.util;

import java.rmi.Naming;
import java.rmi.Remote;

import com.ai.runner.center.dshm.api.dshmservice.interfaces.IdshmreadSV;
import com.ai.runner.center.dshm.api.dshmservice.interfaces.IserviceFactorySV;

public class ServiceRegiter {
	/**
	 * 注册服务
	 * 
	 * @param host
	 * @param port
	 * @param serviceCode
	 * @return
	 * @throws Exception
	 */
	public static final Object registerService(String host,String port,Integer serviceCode) throws Exception{
		String service = ShmConstants.ShmClientInfo.SERVICE_FACTORY_PATH.replace(ShmConstants.ShmClientInfo.SHM_SERVER_HOST, host).replace(ShmConstants.ShmClientInfo.SHM_SERVER_PORT, port);
		System.out.println("the service is :"+service);
		IserviceFactorySV factory = (IserviceFactorySV) Naming.lookup(service);
		switch(serviceCode){
			case ShmConstants.ShmServiceCode.SHM_SERVICE_CODE:
				return (IdshmreadSV)factory.registerService(ShmConstants.ShmServiceCode.SHM_SERVICE_CODE);
			default:throw new IllegalArgumentException("服务编码不存在，请选择提供的服务进行注册！");
		}
	}
}
