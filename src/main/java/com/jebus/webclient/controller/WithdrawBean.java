package com.jebus.webclient.controller;

import java.util.List;
import java.util.Map;
import java.io.Serializable;
import java.math.BigDecimal;

import org.primefaces.PrimeFaces;
import org.primefaces.model.DialogFrameworkOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.annotation.SessionScope;

import com.jebus.webclient.service.BankService;
import com.jebus.webclient.dto.Coin;
import com.jebus.webclient.dto.WithdrawFund;
import com.jebus.webclient.model.Account;
import com.jebus.webclient.model.Journal;
import com.jebus.webclient.exception.InvalidAmountException;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.ValidatorException;

@Controller
@SessionScope
public class WithdrawBean implements Serializable {

	private static final long serialVersionUID = 5719014284220730823L;
	@Autowired
	private BankService bankSvc;
	private WithdrawFund withdraw;
	
	public WithdrawFund getWithdraw() {
		return withdraw;
	}

	public void setWithdraw(WithdrawFund withdraw) {
		this.withdraw = withdraw;
	}

	public void init() {
		if (null == withdraw) {
			withdraw = WithdrawFund.builder()
					.amount(Coin.builder()
							.currency("kes")
							.build())
					.build();
		}
	}

	@PostConstruct
	public void onCreate() {
	}
	
	public void withdrawFund() {
		Map<Account, Journal> transaction;
		try {
			transaction = bankSvc.withdraw(withdraw);
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
		PrimeFaces.current().dialog().openDynamic("withdrawSuccess", options, 
				Map.of("account", List.of(account.getId().toString()), 
						"journal", List.of(journal.getId().toString())));
		withdraw = null;
		init();
	}
	
	public void checkWithdrawAmount(FacesContext fc, UIComponent comp, BigDecimal amount) throws ValidatorException {
		//validate if account has available balance
	}
	
	@PreDestroy
	public void onDestroy() {		
	}
}
