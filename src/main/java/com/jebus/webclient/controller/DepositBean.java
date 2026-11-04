package com.jebus.webclient.controller;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import java.io.Serializable;
import java.util.Map;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.context.annotation.SessionScope;
import org.springframework.beans.factory.annotation.Autowired;

import org.primefaces.PrimeFaces;
import org.primefaces.model.DialogFrameworkOptions;

import jakarta.faces.application.FacesMessage;

import com.jebus.webclient.model.Account;
import com.jebus.webclient.model.Journal;
import com.jebus.webclient.dto.DepositFund;
import com.jebus.webclient.dto.Coin;
import com.jebus.webclient.service.BankService;
import com.jebus.webclient.exception.InvalidAmountException;

@Controller
@SessionScope
public class DepositBean implements Serializable {
	
	private static final long serialVersionUID = 5614509262455046255L;
	@Autowired
	private BankService bankSvc;
	private DepositFund deposit;	

	public DepositFund getDeposit() {
		return deposit;
	}

	public void setDeposit(DepositFund deposit) {
		this.deposit = deposit;
	}
	
	public void init() {
		if (null == deposit) {
			deposit = DepositFund.builder()
					.amount(Coin.builder()
							.currency("kes")
							.build())
					.build();
		}
	}

	@PostConstruct
	public void onCreate() {
	}
	
	public void depositFund() {
		Map<Account, Journal> transaction;
		try {
			transaction = bankSvc.deposit(deposit);
		} catch (InvalidAmountException ex) {
			FacesMessage message = new FacesMessage();
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			message.setDetail(ex.getMessage());
			PrimeFaces.current().dialog().showMessageDynamic(message);
			return;
		}
		Map.Entry<Account,Journal> entry = transaction.entrySet().stream().findFirst().get();
		Account account = entry.getKey();
		Journal journal = entry.getValue();
		DialogFrameworkOptions options = DialogFrameworkOptions.builder()
                .modal(true)
                .width("640")
                .height("340")
                .contentHeight("100%")
                .contentWidth("100%")
                .headerElement("customheader")
                .build();
		PrimeFaces.current().dialog().openDynamic("depositSuccess", options, 
				Map.of("account", List.of(account.getId().toString()), 
						"journal", List.of(journal.getId().toString())));
		deposit = null;
		init();
	}
	
	@PreDestroy
	public void onDestroy() {		
	}
}