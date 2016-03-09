package com.ai.runner.center.bmc.core.init;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.runner.center.bmc.core.executor.FailBillHandler;

public class FailBillStartup {

	private static Logger logger = LoggerFactory.getLogger(FailBillStartup.class);
	private static FailBillHandler failBillHandler;
	
	public static void start(){
		if(failBillHandler == null){
			synchronized(FailBillHandler.class){
				if(failBillHandler == null){
					failBillHandler = new FailBillHandler();
					failBillHandler.start();
					logger.debug("错单处理器启动中...");
				}
			}
		}
	}
	
	
}
