package com.jebus.webclient.exception;

public class InvalidAmountException extends RuntimeException {

	public static final long serialVersionUID = 9383922992L;

	public InvalidAmountException(String message) {
		super(message);
	}

	public InvalidAmountException(Throwable cause) {
		super(cause);
	}
}
