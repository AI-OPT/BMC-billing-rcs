package com.ai.runner.center.bmc.core.flow.cost.container;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import com.ai.runner.center.bmc.core.flow.cost.container.strategy.FlushStrategy;
import com.ai.runner.center.bmc.core.flow.cost.container.strategy.TimeFlushStrategy;
import com.ai.runner.center.bmc.core.flow.cost.entity.GroupFieldValue;

/**
 * 统计容器，存放已经统计好的对象
 * 
 * @author 毕希研
 * 
 * @param <E>
 *            统计结果对象
 * @param <T>
 *            待统计消息对象
 */
public abstract class StatisticContainer<E, T> implements Serializable {
	protected Map<GroupFieldValue, E> container = new TreeMap<GroupFieldValue, E>(new Comparator<GroupFieldValue>() {

		@Override
		public int compare(GroupFieldValue o1, GroupFieldValue o2) {
			return o1.getId().compareTo(o2.getId());
		}
		
	});
	protected FlushStrategy flushStrategy = new TimeFlushStrategy(1000); // 默认使用时间刷新策略

	public abstract void pushToStatistic(T t);

	/**
	 * 取出容器内的统计对象，清空容器，并重置获取时间
	 * 
	 * @return 容器内的统计对象
	 */
	public Collection<E> fetchContainer() {
		Collection<E> col = new ArrayList<E>();
		for (E e : container.values())
			col.add(e);
		container.clear();
		flushStrategy.flushOver();
		return col;
	}

	public Map<GroupFieldValue, E> fetchContainerMap() {
		Map<GroupFieldValue, E> map = new HashMap<>();
		map.putAll(container);
		container.clear();
		flushStrategy.flushOver();
		return map;
	}

	/**
	 * 判断是否需要刷新容器，如果为true，则需要调用fetchContainer
	 * 
	 * @return
	 */
	public boolean isNeedToFlush() {
		return flushStrategy.isNeedToFlush();
	}
}
