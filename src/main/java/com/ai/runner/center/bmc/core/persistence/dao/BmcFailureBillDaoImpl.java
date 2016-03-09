package com.ai.runner.center.bmc.core.persistence.dao;

import com.ai.runner.center.bmc.core.persistence.entity.BmcFailureBill;
import com.ai.runner.center.bmc.core.util.JdbcTemplate;

public class BmcFailureBillDaoImpl implements BmcFailureBillDao {

	@Override
	public int insertFailureBill(BmcFailureBill failBill){
		StringBuilder strSql = new StringBuilder();
		strSql.append("insert into bmc_failure_bill(");
		strSql.append("system_id,service_id,tenant_id,psn,sn,");
		strSql.append("fail_step,fail_code,fail_reason,fail_packet,fail_date");
		strSql.append(") values(?,?,?,?,?,?,?,?,?,?) ");
		
		Object[] params = new Object[10];
		params[0] = failBill.getSystemId();
		params[1] = failBill.getServiceId();
		params[2] = failBill.getTenantId();
		params[3] = failBill.getPsn();
		params[4] = failBill.getSn();
		//params[5] = failBill.getPacketCreateDate();
		params[5] = failBill.getFailStep();
		params[6] = failBill.getFailCode();
		params[7] = failBill.getFailReason();
		params[8] = failBill.getFailPakcet();
		params[9] = failBill.getFailDate();
		
		return JdbcTemplate.update(strSql.toString(), params);
	}
	
	
	
}
