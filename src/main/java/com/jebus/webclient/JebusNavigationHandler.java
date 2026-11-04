package com.jebus.webclient;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.faces.context.FacesContext;
import jakarta.faces.application.NavigationCase;
import jakarta.faces.application.ConfigurableNavigationHandler;

import org.springframework.web.util.UriTemplate;

import org.primefaces.application.DialogNavigationHandler;

public class JebusNavigationHandler extends ConfigurableNavigationHandler {

	protected static final ThreadLocal<AtomicBoolean> isDialog = ThreadLocal.withInitial(() -> new AtomicBoolean(false));
	private UriTemplate bank, auth;
	private DialogNavigationHandler dialogNavigationHandler;
	
	public JebusNavigationHandler(ConfigurableNavigationHandler handler) {
		dialogNavigationHandler = new DialogNavigationHandler(handler);
		bank = new UriTemplate("/bank{/:.*}"); 
		auth = new UriTemplate("/auth{/:.*}");
	}
	
	@Override
	public void handleNavigation(FacesContext fc, String method, String outcome) {	
		AtomicBoolean myAtomic = isDialog.get();		
		try {	
			if (null != outcome && (bank.matches(outcome) || auth.matches(outcome))) {
				myAtomic.compareAndSet(true, false);
				try {
					fc.getExternalContext().redirect(outcome);
				} catch (IOException ioex) {
					ioex.printStackTrace(System.out);
				}
			} else {
				myAtomic.compareAndSet(false, true);
				dialogNavigationHandler.handleNavigation(fc, method, outcome);
			}
		} finally {
			myAtomic.compareAndSet(true, false);
		}			
	}

	@Override
	public NavigationCase getNavigationCase(FacesContext fc, String method, String outcome) {
		return dialogNavigationHandler.getNavigationCase(fc, method, outcome);
	}
	
	@Override
	public Map<String, Set<NavigationCase>> getNavigationCases() {
		return dialogNavigationHandler.getNavigationCases();
	}
}
