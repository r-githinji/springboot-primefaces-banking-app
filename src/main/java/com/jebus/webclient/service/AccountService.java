package com.jebus.webclient.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.jebus.webclient.model.Customer;
import com.jebus.webclient.model.Account;
import com.jebus.webclient.model.Entry;
import com.jebus.webclient.repository.AccountRepository;
import com.jebus.webclient.repository.EntryRepository;

@Service
public class AccountService {

	@Autowired
	private AccountRepository accRepo;
	@Autowired
	private EntryRepository entryRepo;
	
	public List<Account> list(Customer customer) {
		return accRepo.list(customer);
	}
	
	public Account fetch(String number, Customer customer) {
		return accRepo.fetch(number, customer);
	}
	
	public List<Entry> entries(Account account) {
		return entryRepo.list(account);
	}
}
