package com.ai.runner.center.bmc.core.flow.cost.container.strategy;

/**
 * 时间刷新策略
 * 
 * @author bixy
 *
 */
public class TimeFlushStrategy implements FlushStrategy {
	protected int timeMetric = 1000;// 刷新容器时间步长
	private long lastFetchTime = 0L;

	public TimeFlushStrategy(int timeMetric) {
		this.timeMetric = timeMetric;
	}

	@Override
	public boolean isNeedToFlush() {
		if (lastFetchTime + timeMetric < System.currentTimeMillis())
			return true;
		else
			return false;
	}

	@Override
	public void flushOver() {
		long now = System.currentTimeMillis();
		lastFetchTime = now - (now % timeMetric);
	}

	// public static void main(String[] args)
	// {
	// long now = System.currentTimeMillis();
	// System.out.println(now);
	// System.out.println(now - (now % 1000));
	// }
}
