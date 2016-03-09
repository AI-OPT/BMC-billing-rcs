package com.ai.runner.center.bmc.core.util;

public class ShmConstants {
	/**
	 * 服务端的信息
	 * @author biancx
	 *
	 */
	public static final class ShmClientInfo{
		/**
		 * rmc采用协议
		 */
		public static final String SHM_SERVER_PROTOCAL = "rmi";
		/**
		 * 服务主机地址
		 */
		public static final String SHM_SERVER_HOST = "${HOST}";
		/**
		 * 端口
		 */
		public static final String SHM_SERVER_PORT = "${PORT}"; 
		/**
		 * 服务名
		 */
		public static final String SERVICE_FACTORY_NAME = "serviceFactory";
		/**
		 * 服务前缀
		 */
		public static final String SERVICE_PREFIX = SHM_SERVER_PROTOCAL + "://" + SHM_SERVER_HOST + ":" + SHM_SERVER_PORT + "/";
		/**
		 * 服务url
		 */
		public static final String SERVICE_FACTORY_PATH = SERVICE_PREFIX + SERVICE_FACTORY_NAME;
	}
	public static final class ShmServiceCode{

		/**
		 * 共享内存服务编码
		 */
		public static final int SHM_SERVICE_CODE = 10001;
	}
}
