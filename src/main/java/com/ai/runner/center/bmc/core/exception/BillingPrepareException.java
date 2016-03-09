package com.ai.runner.center.bmc.core.exception;

public class BillingPrepareException extends RuntimeException {

	public BillingPrepareException() {
		super();
	}

	public BillingPrepareException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BillingPrepareException(String message, Throwable cause) {
		super(message, cause);
	}

	public BillingPrepareException(String message) {
		super(message);
	}

	public BillingPrepareException(Throwable cause) {
		super(cause);
	}

}
