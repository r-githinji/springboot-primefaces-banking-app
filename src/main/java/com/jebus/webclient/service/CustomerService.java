package com.jebus.webclient.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import com.jebus.webclient.model.Customer;
import com.jebus.webclient.repository.CustomerRepository;
import com.jebus.webclient.repository.AccountRepository;

@Service
public class CustomerService {
	
	@Autowired
	private CustomerRepository custRepo;
	@Autowired
	private AccountRepository accRepo;
	
	public Customer findById(Integer id) {
		return custRepo.findById(id);
	}
	
	public Customer findByReference(String reference) {
		return custRepo.findByReference(reference);
	}

	public Customer create() {
		Customer customer = new Customer();
		customer.setReference(custRepo.series());
		return customer;
	}			
	
	@Transactional
	public Customer create(Customer customer) {
		Customer created = custRepo.create(customer);
		//create chart of accounts for customer
		accRepo.create(created);
		return created;
	}
	
	public int count() {
		return custRepo.count();
	}
	
	public List<Customer> list(int offset, int limit) {
		return custRepo.list(offset, limit);
	}
	
	@Transactional
	public String delete(Customer customer) {
		return custRepo.delete(customer);
	}
}
