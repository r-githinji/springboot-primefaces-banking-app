package com.jebus.webclient.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.faces.context.FacesContext;
import jakarta.faces.component.UIComponent;
import jakarta.faces.validator.ValidatorException;
import jakarta.faces.application.FacesMessage;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.jebus.webclient.service.CustomerService;
import com.jebus.webclient.model.Customer;

@Controller
@SessionScope
public class CustomerBean {
	
	@Autowired
	private CustomerService custSvc;
	private Customer customer;		

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	@PostConstruct
	public void onCreate() {
	}
	
	public void init() {
		if(null == customer) {
			customer = custSvc.create();
		}
	}
	
	public String create() {
		customer = custSvc.create(customer);
		customer = null;
		return "/bank/account/list";
	}
	
	public void checkDateOfBirth(FacesContext fc, UIComponent comp, LocalDate dob) throws ValidatorException {
		long age = dob.until(LocalDate.now(), ChronoUnit.YEARS);
		if (age < 18) {
			FacesMessage message = new FacesMessage();
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			message.setDetail("Only adults aged 18 years or above can legally hold a bank acount");
			throw new ValidatorException(message);
		}
	}
	
	@PreDestroy
	public void onDestroy() {		
	}
}
