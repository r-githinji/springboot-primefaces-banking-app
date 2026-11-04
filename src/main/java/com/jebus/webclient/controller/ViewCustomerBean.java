package com.jebus.webclient.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.annotation.SessionScope;

import com.jebus.webclient.service.CustomerService;
import com.jebus.webclient.service.AccountService;
import com.jebus.webclient.model.Customer;
import com.jebus.webclient.model.Account;
import com.jebus.webclient.model.Entry;
import com.jebus.webclient.utils.JebusUtils;

import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.faces.context.FacesContext;

@Controller
@SessionScope
public class ViewCustomerBean {
	
	@Autowired
	private CustomerService custSvc;
	@Autowired
	private AccountService accSvc;
	private Customer customer;
	private Account account;
	private List<Entry> entries;

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	
	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public List<Entry> getEntries() {
		return entries;
	}

	public void setEntries(List<Entry> entries) {
		this.entries = entries;
	}

	public void init() {
		FacesContext fc = FacesContext.getCurrentInstance();
		Integer id = JebusUtils.evaluateVE(fc, "#{param.id}", Integer.class);
		if (null == id) {
			//invalid parameter|parameter 'id' not supplied
		}
		customer = custSvc.findById(id);
		if (null == customer) {
			//404 not found
		}
		account = accSvc.fetch("2001", customer);
		entries = accSvc.entries(account);
	}

	@PostConstruct
	public void onCreate() {		
	}
	
	public void delete() {	
		custSvc.delete(customer);
	}
	
	@PreDestroy
	public void onDestroy() {		
	}
}
