package com.ai.runner.center.bmc.core.dataquality;

import java.util.HashMap;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class ScriptProcessor {

	private static Logger logger = LoggerFactory.getLogger(ScriptProcessor.class);
	private ScriptEngineManager engineMgr = new ScriptEngineManager();
	private static final String SCRIPT_NAME = "Groovy";
	private ScriptEngine engine = null;
	private Invocable invocableEngine = null;
	private String script;
	
	public ScriptProcessor(String script){
		this.script = script;
		init();
	}
	
	private void init(){
		try{
			engine = engineMgr.getEngineByName(SCRIPT_NAME);
			if(engine == null){
				throw new Exception("Can not initialize "+SCRIPT_NAME+" engine!");
			}
			Object rtn = engine.eval(script);
			if(rtn != null){
				logger.debug(rtn.toString());
			}
			if (!(engine instanceof Invocable)) {
				throw new Exception("Engine does not support Invocable interface!");
			}
			invocableEngine = (Invocable)engine;
		}catch(Exception e){
			logger.error("context",e);
		}
	}
	
	public void reloadScript(String script){
		this.script = script;
		init();
	}
	
	public Object execute(String methodName, Map<String,String> data){
		Object rtnValue = null;
		try{
			rtnValue = invocableEngine.invokeFunction(methodName, data);
		}catch(Exception e){
			logger.error("context",e);
		}
		return rtnValue;
	}
	
	public static void main(String[] args) {
		Map<String,String> data = new HashMap<String,String>();
	    data.put("name", "zhangsan");
	    data.put("age", "25");
		String script = "def sayHello(data){println 'Hello,I am ' + data.get('name') + ',age' + data.get('age');return '0';}";
		ScriptProcessor scriptProcessor = new ScriptProcessor(script);
		Object obj = scriptProcessor.execute("sayHello", data);
		System.out.println(obj instanceof Integer);
	}
	
}
