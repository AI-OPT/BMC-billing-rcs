package com.ai.runner.center.bmc.core.persistence.dao;

import com.ai.runner.center.bmc.core.persistence.entity.BmcFailureBill;

public interface BmcFailureBillDao {
	
	String name = "BmcFailureBillDao";
	
	int insertFailureBill(BmcFailureBill failBill);
}
