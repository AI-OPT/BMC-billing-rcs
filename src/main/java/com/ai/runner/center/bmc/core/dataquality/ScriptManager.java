package com.ai.runner.center.bmc.core.dataquality;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptManager {

	private static Logger logger = LoggerFactory.getLogger(ScriptManager.class);
	private static Map<String,ScriptProcessor> scriptMap = new ConcurrentHashMap<String,ScriptProcessor>();
	private static ScriptManager instance = new ScriptManager();
	
	public static ScriptManager getInstance(){
		return instance;
	}
	
	public boolean addScriptProcessor(String key, String script){
		if(StringUtils.isBlank(script)){
			return false;
		}
		ScriptProcessor scriptProcessor = new ScriptProcessor(script);
		scriptMap.put(key, scriptProcessor);
		return true;
	}
	
	public void clearAll(){
		scriptMap.clear();
	}

	public Object executeScript(String scriptKey,Map<String,String> data){
		ScriptProcessor scriptProcessor = scriptMap.get(scriptKey);
		if(scriptProcessor != null){
			return scriptProcessor.execute("format", data);
		}else{
			return null;
		}
	}

}
