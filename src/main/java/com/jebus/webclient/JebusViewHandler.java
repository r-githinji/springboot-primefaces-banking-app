package com.jebus.webclient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import jakarta.faces.context.FacesContext;
import jakarta.faces.application.ViewHandler;
import jakarta.faces.application.ViewHandlerWrapper;

import org.primefaces.application.DialogViewHandler;

public class JebusViewHandler extends ViewHandlerWrapper {
	
	public static final ThreadLocal<String> action = new ThreadLocal<>();
	private DialogViewHandler dialogViewHandler;
	
	public JebusViewHandler(ViewHandler wrapped) {
		super(wrapped);
		dialogViewHandler = new DialogViewHandler(wrapped);
	}
	
	@Override
	public String getActionURL(FacesContext fc, String view) {	
		AtomicBoolean myAtomic = JebusNavigationHandler.isDialog.get();
		if(myAtomic.get()) {
			return view;
		} else {
			return action.get();
		}
	}

	@Override
	public String getBookmarkableURL(FacesContext context, String viewId, Map<String, List<String>> parameters, boolean includeViewParams) {
		String url = dialogViewHandler.getBookmarkableURL(context, viewId, parameters, includeViewParams);	
		return url.replace(".xhtml", "");
	}
}