package com.jebus.webclient.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.context.annotation.RequestScope;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.Authentication;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.faces.context.FacesContext;
import jakarta.faces.context.ExternalContext;

import com.jebus.webclient.JebusViewHandler;
import com.jebus.webclient.model.User;

@Controller
@RequestScope
public class ProfileBean {

	private User user;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@PostConstruct
	public void onCreate() {
		SecurityContext sc = SecurityContextHolder.getContext();
		Authentication authentication = sc.getAuthentication();
		user = (User)authentication.getPrincipal();
	}
	
	public String logout() {
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();
		ec.invalidateSession();
		return String.format("%s?faces-redirect=true", JebusViewHandler.action.get());
	}
	
	@PreDestroy
	public void onDestroy() {		
	}
}
