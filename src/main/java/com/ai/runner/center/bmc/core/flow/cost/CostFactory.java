package com.ai.runner.center.bmc.core.flow.cost;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.ai.runner.center.bmc.core.entity.JdbcParam;
import com.ai.runner.center.bmc.core.flow.cost.billingto.IBillingTo;
import com.ai.runner.center.bmc.core.flow.cost.billingto.impl.BillingToMysql;
import com.ai.runner.center.bmc.core.flow.cost.billingto.impl.BillingToRedis;
import com.ai.runner.center.bmc.core.flow.cost.container.strategy.EventFlushStrategy;
import com.ai.runner.center.bmc.core.flow.cost.container.strategy.FlushStrategy;
import com.ai.runner.center.bmc.core.flow.cost.container.strategy.TimeFlushStrategy;
import com.ai.runner.center.bmc.core.flow.cost.credit.ICreditControl;
import com.ai.runner.center.bmc.core.flow.cost.credit.impl.MDSCreditControl;
import com.ai.runner.center.bmc.core.flow.cost.credit.impl.PrintCreditControl;
import com.ai.runner.center.bmc.core.flow.cost.resourcebook.IAccessResourceBook;
import com.ai.runner.center.bmc.core.flow.cost.resourcebook.impl.MDSAccessResourceBook;
import com.ai.runner.center.bmc.core.flow.cost.resourcebook.impl.PrintAccessResourceBook;

/**
 * 计费工厂类，复制产生计费过程中各种可选择的类
 * 
 * @author bixy
 * 
 */
public class CostFactory {
	private static Logger logger = Logger.getLogger(CostFactory.class);

	public static IBillingTo getBillingDestination(Map<String, String> conf) {
		String destination = (String) conf.get(CostCalculateHandler.BILLING_DESTINATION);
		JdbcParam jdbcParam = JdbcParam.getInstance(conf);
		if ("mysql".equals(destination)) {
			return new BillingToMysql(jdbcParam);
		} else if ("redis".equals(destination))
			return new BillingToRedis();
		else
			throw new UnsupportedOperationException(destination + " does't be support yet!");
	}

	public static ICreditControl getCreditControl(String type, String param) {
		if (StringUtils.isEmpty(type)) {
			logger.warn(ICreditControl.CREDIT_CONTROL + " does't be set");
			throw new IllegalArgumentException(ICreditControl.CREDIT_CONTROL + " does't be set");
		}
		if ("mds".equals(type))
			return new MDSCreditControl(param);
		else if ("print".equals(type))
			return new PrintCreditControl();
		else
			throw new UnsupportedOperationException(ICreditControl.CREDIT_CONTROL + " : " + type + " does't be support yet!");
	}

	public static IAccessResourceBook getAccessResourceBook(String type, String param) {
		if (StringUtils.isEmpty(type)) {
			logger.warn(IAccessResourceBook.ACCESS_RESOURCEBOOK + " does't be set");
			throw new IllegalArgumentException(IAccessResourceBook.ACCESS_RESOURCEBOOK + " does't be set");
		}
		if ("mds".equals(type))
			return new MDSAccessResourceBook(param);
		else if ("print".equals(type))
			return new PrintAccessResourceBook();
		else
			throw new UnsupportedOperationException(IAccessResourceBook.ACCESS_RESOURCEBOOK + " : " + type + " does't be support yet!");
	}

	public static FlushStrategy getFlushStrategy(String type, String param) {
		if (StringUtils.isEmpty(type)) {
			logger.warn(FlushStrategy.BILLING_CONTAINER_FLUSH_STRATEGY + " does't be set");
			throw new IllegalArgumentException(FlushStrategy.BILLING_CONTAINER_FLUSH_STRATEGY + " does't be set");
		}
		if ("time".equals(type))
			return new TimeFlushStrategy(1000);
		else if ("event".equals(type))
			return new EventFlushStrategy();
		else
			throw new UnsupportedOperationException(FlushStrategy.BILLING_CONTAINER_FLUSH_STRATEGY + " : " + type + " does't be support yet!");
	}
}
