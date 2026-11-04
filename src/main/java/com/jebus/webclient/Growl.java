package com.jebus.webclient;

public class Growl {

	private String severity, message;

	private Growl(String severity, String message) {
		this.severity = severity;
		this.message = message;
	}
	
	public String getSeverity() {
		return severity;
	}

	public String getMessage() {
		return message;
	}

	public static synchronized Growl info(String message) {
		return new Growl("info", message);
	}

	public static synchronized Growl warn(String message) {
		return new Growl("warn", message);
	}

	public static synchronized Growl error(String message) {
		return new Growl("error", message);
	}

	public static synchronized Growl success(String message) {
		return new Growl("success", message);
	}

}
