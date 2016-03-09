package com.ai.runner.center.bmc.core.executor;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.runner.center.bmc.core.persistence.dao.BmcFailureBillDao;
import com.ai.runner.center.bmc.core.persistence.entity.BmcFailureBill;
import com.ai.runner.center.bmc.core.util.DaoFactory;

/**
 * 错单处理器
 * @author majun
 *
 */
public class FailBillHandler extends LoopThread {

	private static Logger logger = LoggerFactory.getLogger(FailBillHandler.class);
	public static BlockingQueue<BmcFailureBill> msgQueue = new LinkedBlockingQueue<BmcFailureBill>();
	private BmcFailureBillDao bmcFailureBillDao = null;
	
	@Override
	public boolean init() {
		//bmcFailureBillDao = (BmcFailureBillDao)ApplicationContextUtil.getBean("bmcFailureBillDao");
		bmcFailureBillDao = (BmcFailureBillDao)DaoFactory.getInstance(BmcFailureBillDao.name);
		return true;
	}

	@Override
	public boolean unInit() {
		return true;
	}

	@Override
	public void work() {
		BmcFailureBill element = null;
		logger.debug("---------------FailBillHandler.begin");
		try{
			element = msgQueue.take();
		}catch(InterruptedException e){
			logger.error("context", e);
			exitFlag = true;
		}
		logger.debug("---------------FailBillHandler.end");
		bmcFailureBillDao.insertFailureBill(element);
	}

}
