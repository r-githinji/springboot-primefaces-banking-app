package com.jebus.webclient.controller;

import java.io.Serializable;

import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.ValidatorException;
import jakarta.validation.constraints.NotEmpty;

import com.jebus.webclient.model.Authority;
import com.jebus.webclient.model.User;
import com.jebus.webclient.service.UserService;
import com.jebus.webclient.utils.JebusUtils;

@Controller
@Scope("view")
public class SignupBean implements Serializable {

	private static final long serialVersionUID = 373632832L;
	@Autowired
	private UserService usrSvc;
	private User user;
	private String confirm;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	@NotEmpty(message = "{signup.password.confirm}")
	public String getConfirm() {
		return confirm;
	}

	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}

	@PostConstruct
	public void onCreate() {
		user = usrSvc.create(Authority.AuthorityType.TELLER);
	}
	
	public String register() {
		user.setPassword(JebusUtils.hash(confirm));
		user.setEnabled(true);
		user = usrSvc.create(user);
		return "/auth/signin";
	}
	
	public void checkUsername(FacesContext fc, UIComponent comp, String value) throws ValidatorException {
		User stored = usrSvc.findByName(value);
		if (null != stored) {
			FacesMessage message = new FacesMessage();
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			message.setDetail(String.format("Username %s not available", value));
			throw new ValidatorException(message);
		}
	}
	
	public void checkConfirmPassword(FacesContext fc, UIComponent comp, String value) throws ValidatorException {
		String password = user.getPassword();
		if (null == password || password.isEmpty() ) {
			FacesMessage message = new FacesMessage();
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			message.setDetail("password value is required");
			throw new ValidatorException(message);
		} else if (!JebusUtils.hash(value).contentEquals(JebusUtils.hash(password))) {
			FacesMessage message = new FacesMessage();
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			message.setDetail("passwords entered do not match");
			throw new ValidatorException(message);
		}
	}
	
	@PreDestroy
	public void onDestroy() {		
	}
}