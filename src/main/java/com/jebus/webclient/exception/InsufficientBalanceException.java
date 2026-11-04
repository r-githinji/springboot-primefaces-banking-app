package com.jebus.webclient.exception;

import com.jebus.webclient.Growl;

public class InsufficientBalanceException extends RuntimeException {

	private static final long serialVersionUID = 73832892L;

	public InsufficientBalanceException(String message) {
		super(message);
	}

	public Growl getGrowl() {
		return Growl.error(getMessage());
	}

}
