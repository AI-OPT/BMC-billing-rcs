package com.ai.runner.center.bmc.core.flow.cost.container.strategy;

/**
 * 容器的刷新策略接口
 * 
 * @author bixy
 *
 */
public interface FlushStrategy {
	public static final String BILLING_CONTAINER_FLUSH_STRATEGY = "billing.container.flush.strategy";

	/**
	 * 是否需要刷新
	 * 
	 * @return
	 */
	public boolean isNeedToFlush();

	/**
	 * 刷新结束
	 */
	public void flushOver();
}
