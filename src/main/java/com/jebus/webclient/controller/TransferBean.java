package com.jebus.webclient.controller;

import java.util.List;
import java.util.Map;

import org.primefaces.PrimeFaces;
import org.primefaces.model.DialogFrameworkOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.context.annotation.SessionScope;

import jakarta.faces.context.FacesContext;
import jakarta.faces.component.UIComponent;
import jakarta.faces.validator.ValidatorException;
import jakarta.faces.application.FacesMessage;

import com.jebus.webclient.model.Account;
import com.jebus.webclient.model.Journal;
import com.jebus.webclient.model.Customer;
import com.jebus.webclient.dto.Coin;
import com.jebus.webclient.dto.TransferFund;
import com.jebus.webclient.service.BankService;
import com.jebus.webclient.exception.InvalidAmountException;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Controller
@SessionScope
public class TransferBean {

	@Autowired
	private BankService bankSvc;
	private TransferFund transfer;

	public TransferFund getTransfer() {
		return transfer;
	}

	public void setTransfer(TransferFund transfer) {
		this.transfer = transfer;
	}
	
	public void init() {
		if (null == transfer) {
			transfer = TransferFund.builder()
					.amount(Coin.builder()
							.currency("kes")
							.build())
					.build();
		}
	}

	@PostConstruct
	public void onCreate() {
	}
	
	public void transferFund() {
		Map<Account, Journal> transaction;
		try {
			transaction = bankSvc.transfer(transfer);
		} catch (InvalidAmountException ex) {
			FacesMessage message = new FacesMessage();
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			message.setDetail(ex.getMessage());
			PrimeFaces.current().dialog().showMessageDynamic(message);
			return;
		}
		Account account = transaction.entrySet().stream().findFirst().get().getKey();
		account.getClass();
		DialogFrameworkOptions options = DialogFrameworkOptions.builder()
                .modal(true)
                .width("640")
                .height("340")
                .contentHeight("100%")
                .contentWidth("100%")
                .headerElement("customheader")
                .build();
		PrimeFaces.current().dialog().openDynamic("transferSuccess", options, 
				Map.of("account", List.of(String.valueOf(account.getId()))));
		transfer = null;
		init();
	}
	
	public void checkSource(FacesContext fc, UIComponent comp, Customer source) throws ValidatorException {
		Customer target = transfer.getTarget();
		if (source.equals(target)) {
			FacesMessage message = new FacesMessage();
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			message.setDetail("Source account cannot be same as destination account");
			throw new ValidatorException(message);
		}
	}
	
	public void checkTarget(FacesContext fc, UIComponent comp, Customer target) throws ValidatorException {
		Customer source = transfer.getTarget();
		if (target.equals(source)) {
			FacesMessage message = new FacesMessage();
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			message.setDetail("Destination account cannot be same as source account");
			throw new ValidatorException(message);
		}
	}
	
	@PreDestroy
	public void onDestroy() {		
	}
}
