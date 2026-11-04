package com.jebus.webclient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.jebus.webclient.service.CustomerService;
import com.jebus.webclient.service.AccountService;
import com.jebus.webclient.service.BankService;
import com.jebus.webclient.model.Customer;
import com.jebus.webclient.model.Account;
import com.jebus.webclient.model.Journal;
import com.jebus.webclient.model.Entry;
import com.jebus.webclient.dto.DepositFund;
import com.jebus.webclient.dto.WithdrawFund;
import com.jebus.webclient.dto.TransferFund;
import com.jebus.webclient.dto.Coin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootTest(classes = {JebusBankApplication.class})
class JebusBankApplicationTests {
	
	private static final Logger logger = LoggerFactory.getLogger(JebusBankApplicationTests.class);	
	@Autowired
	private CustomerService custSvc;
	@Autowired
	private AccountService accSvc;
	@Autowired
	private BankService bankSvc;

	@Test
	void contextLoads() {
		Customer bank = custSvc.findByReference("JEBUS-001");
		
		Customer customer = custSvc.create();
		customer.setFirstName("Source");
		customer.setLastName("Customer");
		customer.setPhoneNumber("0752***019");
		customer.setEmail("source.customer@gmail.com");
		customer.setBirthDate(LocalDate.of(1998, 3, 17));		
		customer = custSvc.create(customer);
		
		deposit(customer, 100d);
		display("JEBUS BANK", bank);
		
		withdraw(customer, 75d);
		display("JEBUS BANK", bank);
		
		Customer target = custSvc.create();
		target.setFirstName("Target");
		target.setLastName("Customer");
		target.setPhoneNumber("0725362637");
		target.setEmail("target.customer@gmail.com");
		target.setBirthDate(LocalDate.of(1972, 3, 25));
		target = custSvc.create(target);
		
		transfer(customer, target, 15);
		display("JEBUS BANK", bank);
	}
	
	void display(String action, Customer customer) {
		StringBuilder sb = new StringBuilder()
		.append("\n\n")
		.repeat("*", 45).append("\n")
		.append("[").append(action).append("] - ").append(customer.getName()).append("[").append(customer.getReference()).append("]").append("\n")
		.repeat("*", 45).append("\n\n")
		.append(String.format("%7s %20s %12s", "Account", "Description", "Balance"))
		.append("\n");	
		List<Account> accounts = accSvc.list(customer);
		for(Account account: accounts) {
			sb.append(String.format("%7s %20s %12s", account.getNumber(), account.getDescription(), account.getBalance())).append("\n");
		}
		logger.info("{}", sb.append("\n").repeat("*", 45));
	}
	
	void deposit(Customer customer, double amount) {
		DepositFund deposit = DepositFund.builder()
				.amount(Coin.builder()
						.amount(BigDecimal.valueOf(amount))
						.currency("kes")
						.build())
				.target(customer)
			.build();		
		Map<Account, Journal> transaction = bankSvc.deposit(deposit);		
		display("DEPOSIT", customer);
		Account account = transaction.entrySet().stream().findFirst().get().getKey();
		if (!"".isEmpty())
			entries(account);
	}
	
	void withdraw(Customer customer, double amount) {
		WithdrawFund withdrawal = WithdrawFund.builder()
				.amount(Coin.builder()
						.amount(BigDecimal.valueOf(amount))
						.currency("kes")
						.build())
				.source(customer)
			.build();
		Map<Account, Journal> transaction = bankSvc.withdraw(withdrawal);	
		display("AFTER WITHDRAWAL", customer);
		Account account = transaction.entrySet().stream().findFirst().get().getKey();
		if (!"".isEmpty())
			entries(account);
	}
	
	void transfer(Customer source, Customer target, double amount) {
		TransferFund fund = TransferFund.builder()
				.source(source)
				.target(target)
				.amount(Coin.builder()
						.amount(BigDecimal.valueOf(amount))
						.currency("kes")
						.build())
				.build();
		Map<Account, Journal> transaction = bankSvc.transfer(fund);	
		display("SOURCE AFTER TRANSFER", source);
		display("TARGET AFTER TRANSFER", target);
		Account account = transaction.entrySet().stream().findFirst().get().getKey();
		if (!"".isEmpty())
			entries(account);
	}
	
	void entries(Account account) {
		StringBuilder sb = new StringBuilder();
		sb.append("\n").repeat("*", 45)
		.append(account.getDescription()).append("[").append(account.getNumber()).append("]").append("\n")
		.repeat("*", 45).append("\n\n")
		.append(String.format("%10s %15s %15s", "Type", "Amount", "Balance"))
		.append("\n");	
		List<Entry> entries = accSvc.entries(account);
		for (Entry entry: entries) {
			sb.append(String.format("%10s %15s %15s", entry.getType(), entry.getAmount(), entry.getBalance())).append("\n");
		}
		logger.info("{}", sb.append("\n").repeat("*", 45));
	}
}
