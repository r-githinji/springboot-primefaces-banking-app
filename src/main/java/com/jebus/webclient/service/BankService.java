package com.jebus.webclient.service;

import java.math.BigDecimal;
import java.util.Map;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.jebus.webclient.model.Entry;
import com.jebus.webclient.model.Account;
import com.jebus.webclient.model.Customer;
import com.jebus.webclient.model.Journal;
import com.jebus.webclient.dto.DepositFund;
import com.jebus.webclient.dto.Coin;
import com.jebus.webclient.dto.WithdrawFund;
import com.jebus.webclient.dto.TransferFund;
import com.jebus.webclient.repository.CustomerRepository;
import com.jebus.webclient.repository.AccountRepository;
import com.jebus.webclient.repository.JournalRepository;
import com.jebus.webclient.exception.InsufficientBalanceException;
import com.jebus.webclient.exception.InvalidAmountException;
import com.jebus.webclient.exception.OutOfBalanceException;
import com.jebus.webclient.Constants;

@Service
public class BankService {
	
	@Autowired
	private CustomerRepository custRepo;
	@Autowired
	private AccountRepository accRepo;
	@Autowired
	private JournalRepository jnlRepo;
	private Customer bank;
	
	@PostConstruct
	public void onCreate() {
		bank = custRepo.findByReference(Constants.JEBUS_BANK_REF);
	}
	
	private Account getBankReserves() {
		bank = custRepo.findByReference(Constants.JEBUS_BANK_REF);
		return accRepo.fetch("1001", bank);
	}
	
	private Entry debit(BigDecimal amount) {
		Account account = getBankReserves();
		return Entry.debit(amount, account);
	}
	
	private Entry credit(BigDecimal amount) {
		Account account = getBankReserves();
		if (account.getBalance().compareTo(amount) < 0) {
			throw new InsufficientBalanceException(String.format("Account %s has insufficient balance to process credit", account.getDescription()));
		}
		return Entry.credit(amount, account);
	}

	@Transactional
	public Map<Account, Journal> deposit(DepositFund deposit) {
		Coin amount = deposit.getAmount();
		if (amount.getValue().compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidAmountException("Invalid deposit amount!");
		}	
		Customer target = deposit.getTarget();
		//create deposit journal
		Journal journal = new Journal(jnlRepo.series(), Journal.JournalType.RECEIPT, Journal.JournalSource.DEPOSIT);	
		//DR bank (asset)
		Entry entry = debit(amount.getValue());
		journal.add(entry);	
		//customer funds/deposits(liability)
		Account funds = accRepo.fetch("2001", target);
		//credit customer funds account
		entry = Entry.credit(amount.getValue(), funds);
		journal.add(entry);		
		//check if deposit journal is balanced
		if (!journal.isBalanced()) {
			throw new OutOfBalanceException(String.format("%s transaction %s is out of balance", journal.getType().getLabel(), journal.getReference()));
		}
		//post journal to general ledger
		journal = jnlRepo.create(journal);
		//read current customer funds account balance
		Account custFunds = accRepo.fetch("2001", target);
		return Map.of(custFunds, journal);

	}
	
	/*
	 * check is amount is with-drawable by the account holder
	 */
	public boolean isWithdrawable(Customer drawer, Coin amount) {
		Account funds = accRepo.fetch("2001", drawer);
		BigDecimal balance = funds.getBalance();
		return balance.compareTo(amount.getValue()) > 0;
	}
	
	@Transactional
	public Map<Account, Journal> withdraw(WithdrawFund withdrawal) {
		Coin amount = withdrawal.getAmount();
		if (amount.getValue().compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidAmountException("Invalid withdrawal amount! Amount should be greater than zero");
		}		
		Customer drawer = withdrawal.getSource();	
		Journal journal = new Journal(jnlRepo.series(), Journal.JournalType.PAYMENT, Journal.JournalSource.WITHDRAWAL);		
		//CR bank reserves
		Entry entry = credit(amount.getValue());
		journal.add(entry);		
		//DR customer funds(liability)
		Account funds = accRepo.fetch("2001", drawer);
		entry = Entry.debit(amount.getValue(), funds);
		journal.add(entry);
		//check if withdrawal journal is balanced
		if (!journal.isBalanced()) {
			throw new OutOfBalanceException(String.format("%s transaction %s is out of balance", journal.getType().getLabel(), journal.getReference()));
		}		
		//post withdrawal journal to general ledger
		journal = jnlRepo.create(journal);		
		//read current customer funds account balance
		Account custFunds = accRepo.fetch("2001", drawer);	
		return Map.of(custFunds, journal);
	}
	
	@Transactional
	public Map<Account, Journal> transfer(TransferFund transfer) {
		Coin amount = transfer.getAmount();
		if (amount.getValue().compareTo(BigDecimal.ZERO) <= 0) {
			throw new InvalidAmountException("Invalid transfer amount!");
		}				
		Customer source = transfer.getSource(), target = transfer.getTarget();
		//sender customer funds (liability)
		Account custFund = accRepo.fetch("2001", source);
		if (custFund.getBalance().compareTo(amount.getValue()) <= 0) {
			throw new OutOfBalanceException(String.format("Account balance not enough to complete transfer of amount %s", amount.getValue()));
		}
		//proceed to create disbursement journal
		Journal journal = new Journal(jnlRepo.series(), Journal.JournalType.PAYMENT, Journal.JournalSource.TRANSFER);
		//reduce receiver customer funds (liability)
		Entry entry = Entry.debit(amount.getValue(), custFund);
		journal.add(entry);
		//CR bank reserves
		entry = credit(amount.getValue());
		journal.add(entry);		
		
		custFund = accRepo.fetch("2001", target);
		//credit receiver customer funds (liability)
		entry = Entry.credit(amount.getValue(), custFund);
		journal.add(entry);		
		//DR bank (asset)
		entry = debit(amount.getValue());
		journal.add(entry);			
		//check if transfer journal is balanced
		if (!journal.isBalanced()) {
			throw new OutOfBalanceException(String.format("%s transaction %s is out of balance", journal.getType().getLabel(), journal.getReference()));
		}		
		//post transfer journal to general ledger
		journal = jnlRepo.create(journal);		
		//read sender's account balance
		custFund = accRepo.fetch("2001", source);
		return Map.of(custFund, journal);
	}
	
	@PreDestroy
	public void onDestroy() {		
	}
}
