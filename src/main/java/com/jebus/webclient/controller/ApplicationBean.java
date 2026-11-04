package com.jebus.webclient.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.context.annotation.ApplicationScope;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.faces.convert.DateTimeConverter;
import jakarta.faces.convert.NumberConverter;

import java.time.LocalDate;
import java.util.List;

import com.jebus.webclient.Constants;
import com.jebus.webclient.model.User;
import com.jebus.webclient.model.Authority;
import com.jebus.webclient.model.Customer;
import com.jebus.webclient.service.CustomerService;
import com.jebus.webclient.service.UserService;
import com.jebus.webclient.utils.JebusUtils;

@Controller
@ApplicationScope
public class ApplicationBean {
	
	@Autowired
	private CustomerService custSvc;
	@Autowired
	private UserService usrSvc;
	private DateTimeConverter dateConverter, timeConverter;
	private NumberConverter currencyConverter;

	public DateTimeConverter getDateConverter() {
		return dateConverter;
	}

	public void setDateConverter(DateTimeConverter dateConverter) {
		this.dateConverter = dateConverter;
	}

	public DateTimeConverter getTimeConverter() {
		return timeConverter;
	}

	public void setTimeConverter(DateTimeConverter timeConverter) {
		this.timeConverter = timeConverter;
	}

	public NumberConverter getCurrencyConverter() {
		return currencyConverter;
	}

	public void setCurrencyConverter(NumberConverter currencyConverter) {
		this.currencyConverter = currencyConverter;
	}
	
	@EventListener(value = {ApplicationReadyEvent.class})
	public void onAppStarted() {
		//load application initial data
		String username = "bank.manager@jebusbank.com";		
		User teller = usrSvc.findByName(username);
		if (null == teller) {
			teller = usrSvc.create(Authority.AuthorityType.MANAGER);
			teller.setEnabled(true);
			teller.setName("Bank Manager");
			teller.setUsername(username);
			teller.setPassword(JebusUtils.hash("pebble123"));
			//save bank teller instance
			teller = usrSvc.create(teller);
		}
		
		//load initial customer/BANK instance
		Customer bank = custSvc.findByReference(Constants.JEBUS_BANK_REF);
		if (null == bank) {
			bank = custSvc.create();
			bank.setFirstName("JEBUS");
			bank.setLastName("BANK");
			bank.setEmail("customerservice@jebusbank.com");
			bank.setPhoneNumber("0724728309");
			bank.setBirthDate(LocalDate.of(2024, 6, 18));
			bank.setReference(Constants.JEBUS_BANK_REF);
			//create bank instance
			bank = custSvc.create(bank);
		}
	}

	@PostConstruct
	public void onCreate() {
		dateConverter = new DateTimeConverter();
		dateConverter.setType("localDate");
		dateConverter.setPattern("yyyy.MM.dd");
		
		timeConverter = new DateTimeConverter();
		timeConverter.setType("localTime");
		timeConverter.setPattern("hh:mm a");
		
		currencyConverter = new NumberConverter();
		currencyConverter.setType("currency");
		currencyConverter.setCurrencySymbol("KES");
		currencyConverter.setMinFractionDigits(2);
		currencyConverter.setMaxFractionDigits(2);
		currencyConverter.setPattern("#,##0.00;(#,##0.00)");
	}
	
	public List<Customer> filterCustomer(String arg) {
		return custSvc.list(0, 10);
	}
	
	public Customer findCustById(Integer id) {
		return custSvc.findById(id);
	}
	
	@PreDestroy
	public void onDestroy() {		
	}
}
