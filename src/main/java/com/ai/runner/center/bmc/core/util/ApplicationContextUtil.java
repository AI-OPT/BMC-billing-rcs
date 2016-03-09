//package com.ai.runner.center.bmc.core.util;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//
//public class ApplicationContextUtil {
//	
//	private static Logger logger = LoggerFactory.getLogger(ApplicationContextUtil.class);
//	private static ApplicationContextUtil instance = null;
//	private static ApplicationContext context = new ClassPathXmlApplicationContext("app-context.xml");
//	private static boolean finished = false;
//	
//	private ApplicationContextUtil(){}
//	
//	public static Object getBean(String beanName) {
//		if (beanName != null && !"".equals(beanName)) {
//			return context.getBean(beanName);
//		}
//		return null;
//	}
//	
////	public static ApplicationContextUtil getInstance(){
////		if(instance == null){
////			synchronized(ApplicationContextUtil.class){
////				if(instance == null){
////					instance = new ApplicationContextUtil();
////					loadResource();
////				}
////			}
////		}
////		return instance;
////	}
////	
////	private static void loadResource(){
////		context = new ClassPathXmlApplicationContext("app-context.xml");
////		logger.debug("ApplicationContext 已加载完成!");
////		finished = true;
////	}
//	
////	public Object getBean(String beanName) {
////		if (beanName != null && !"".equals(beanName)) {
////			return context.getBean(beanName);
////		}
////		return null;
////	}
//	
////	/**
////	 * 同步等待context加载完成
////	 */
////	public void syncWaitForLoading(){
////		while(!finished){
////			try {
////				TimeUnit.SECONDS.sleep(1);
////			} catch (InterruptedException e) {
////				logger.error("context",e);
////			}
////		}
////	}
//	
//}
