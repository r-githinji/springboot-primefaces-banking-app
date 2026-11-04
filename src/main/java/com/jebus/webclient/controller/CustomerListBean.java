package com.jebus.webclient.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.util.List;
import java.util.Map;

import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

import com.jebus.webclient.model.Customer;
import com.jebus.webclient.service.CustomerService;

@Controller
@SessionScope
public class CustomerListBean {
	
	@Autowired
	private CustomerService custSvc;
	private LazyDataModel<Customer> customers;
	private Integer rows;

	public LazyDataModel<Customer> getCustomers() {
		return customers;
	}

	public void setCustomers(LazyDataModel<Customer> customerList) {
		this.customers = customerList;
	}

	public Integer getRows() {
		return rows;
	}

	public void setRows(Integer rows) {
		this.rows = rows;
	}

	@PostConstruct
	public void onCreate() {
		rows = 5;
		customers = new LazyDataModel<>() {
			
			private final static long serialVersionUID = 28233298932L;

			@Override
			public int count(Map<String, FilterMeta> filterBy) {
				return custSvc.count();
			}

			@Override
			public List<Customer> load(int first, int pageSize, Map<String, SortMeta> sortBy,
					Map<String, FilterMeta> filterBy) {
				return custSvc.list(first, pageSize);
			}
		};
	}
	
	@PreDestroy
	public void onDestroy() {		
	}
}
