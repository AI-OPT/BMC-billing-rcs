package com.ai.runner.center.bmc.core.flow;

import java.util.Map;

import org.slf4j.Logger;

import com.ai.paas.ipaas.rcs.bolt.Processor;
import com.ai.paas.ipaas.rcs.common.Fields;
import com.ai.paas.ipaas.rcs.common.FlowContext;
import com.ai.paas.ipaas.rcs.common.ProcessorCollector;
import com.ai.paas.ipaas.rcs.data.StreamData;

/**
 * 一个空的Processor实现
 * 
 * @author bixy
 * 
 */
public class EmptyProcessor extends Processor {

	@Override
	public void execute(StreamData adata) {
	}

	@Override
	public Fields getOutFields() {
		return null;
	}

	@Override
	public void prepare(Map aConf, FlowContext aContext, ProcessorCollector collector) {
	}

	@Override
	public void cleanup() {
	}

	@Override
	public void buildLogger(Logger LOG) {
	}

}
