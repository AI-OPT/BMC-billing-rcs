package com.ai.runner.center.bmc.core.persistence.dao;

import java.sql.Connection;
import java.util.List;

import com.ai.runner.center.bmc.core.persistence.entity.BmcOutputDetail;
import com.ai.runner.center.bmc.core.persistence.entity.BmcOutputInfo;

public interface BmcOutputDao {

	String name = "BmcOutputDao";

	List<BmcOutputInfo> queryAllOutputData(Connection conn);

	List<BmcOutputDetail> queryOutputDetailByInfoCode(Connection conn, String infoCode);

}
