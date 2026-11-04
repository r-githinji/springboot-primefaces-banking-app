package com.jebus.webclient.exception;	

import com.jebus.webclient.Growl;

public class OutOfBalanceException extends RuntimeException {

	private static final long serialVersionUID = 838372289L;
	
	public OutOfBalanceException(String message) {
		super(message);
	}

	public Growl getGrowl() {
		return Growl.error(getMessage());
	}
}
