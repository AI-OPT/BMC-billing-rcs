package com.ai.runner.center.bmc.core.executor;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ai.paas.ipaas.rcs.data.StreamData;
import com.ai.runner.center.bmc.core.persistence.entity.BmcFailureBill;
import com.ai.runner.center.bmc.core.util.BmcException;
import com.ai.runner.center.bmc.core.util.RecordUtil;


/**
 * 错单产生对象
 * @author majun
 *
 */
public class FailBillDeliver {

	private static Logger logger = LoggerFactory.getLogger(FailBillDeliver.class);
	public static final String FAIL_STEP_FORMAT = "pre";
	public static final String FAIL_STEP_RULE = "match";
	public static final String FAIL_STEP_CAL = "cal";
	
	/**
	 * 组装错误数据,发送到错单处理器
	 * @param header 消息头
	 * @param businessData 出错业务数据
	 * @param e 错误信息
	 * @param step 出错环节
	 */
	public static void deliver(Map<String,String> businessData,BmcException e,String step){
		try {
			BmcFailureBill failureBill = new BmcFailureBill();
			failureBill.setSystemId(businessData.get(RecordUtil.SYSTEM_ID));
			failureBill.setServiceId(businessData.get(RecordUtil.SERVICE_ID));
			failureBill.setTenantId(businessData.get(RecordUtil.TENANT_ID));
			//failureBill.setPsn((header.get(PacketUtil.PACKET_SERIAL_NUMBER)));
			failureBill.setPsn(businessData.get("psn"));
			failureBill.setSn(businessData.get("sn"));
			//failureBill.setSource(businessData.get("source"));
			//failureBill.setRow_num(Integer.parseInt(businessData.get("row_num")));
			failureBill.setPacketCreateDate(null);
			failureBill.setFailStep(step);
			failureBill.setFailCode(e.getCode());
			failureBill.setFailReason(e.getStrStackTrace());
			failureBill.setFailPakcet(businessData.toString());
			failureBill.setFailDate(new Timestamp(System.currentTimeMillis()));
			
			FailBillHandler.msgQueue.put(failureBill);
		} catch (Exception ex) {
			logger.error("context", ex);
		}
	}
	
	public static void deliver(String strData,Exception e,String step){
		try {
			BmcFailureBill failureBill = new BmcFailureBill();
			failureBill.setFailStep(step);
			//failureBill.setFailCode(e.getCode());
			failureBill.setFailReason(e.getMessage());
			failureBill.setFailPakcet(strData);
			failureBill.setFailDate(new Timestamp(System.currentTimeMillis()));
			
			FailBillHandler.msgQueue.put(failureBill);
		} catch (Exception ex) {
			System.out.println("FailBillDeliver.deliver------------>>>>>>>>>>>>");
			logger.error("context", ex);
		}
	}
	
	public static void deliver(StreamData sdata,Exception e,String step){
		List<Object> datas = sdata.getValues();
		StringBuilder strData = new StringBuilder();
		for(Object data:datas){
			strData.append(data).append(" ");
		}
		deliver(strData.toString(),e,step);
	}
	
	
}
