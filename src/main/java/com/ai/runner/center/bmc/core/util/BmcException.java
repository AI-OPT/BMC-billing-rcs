package com.ai.runner.center.bmc.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class BmcException extends Exception {

	private static final long serialVersionUID = -4985975715070523516L;
	private String code;
	
	public BmcException(String code, String msg){
		super(msg);
		this.code = code;
	}
	
	public BmcException(String code, String msg, Throwable cause){
		super(msg,cause);
	}
	
	public BmcException(String code, Throwable cause){
		super(cause);
		this.code = code;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getStrStackTrace(){
		StringWriter sw = new StringWriter();
		printStackTrace(new PrintWriter(sw, true));
		return sw.toString();
	}
}
