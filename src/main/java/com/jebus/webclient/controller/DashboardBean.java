package com.jebus.webclient.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.context.annotation.RequestScope;

import com.jebus.webclient.model.Journal;

import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

@Controller
@RequestScope
public class DashboardBean {

	@Autowired
	private EntityManager em;
	private BigDecimal deposits, withdrawals;
	
	public BigDecimal getDeposits() {
		return deposits;
	}

	public void setDeposits(BigDecimal deposits) {
		this.deposits = deposits;
	}

	public BigDecimal getWithdrawals() {
		return withdrawals;
	}

	public void setWithdrawals(BigDecimal withdrawals) {
		this.withdrawals = withdrawals;
	}

	@PostConstruct
	public void onCreate() {		
		deposits = getSumOf(Journal.JournalType.RECEIPT, "2001");
		withdrawals = getSumOf(Journal.JournalType.PAYMENT, "2001");
	}
	
	private BigDecimal getSumOf(Journal.JournalType type, String account) {
		String sql = "SELECT COALESCE(SUM(b.entry_amt), 0) AS acc_bal "
				+ "		FROM jnl a  "
				+ "		JOIN entry b ON b.entry_jnl = a.jnl_id "
				+ "		JOIN acc c ON c.acc_id = b.entry_acc "
				+ "	  WHERE a.jnl_type = ?1 AND c.acc_no = ?2 ";
		Query query = em.createNativeQuery(sql);
		query.setParameter(1, type.getValue());
		query.setParameter(2, account);
		BigDecimal result = (BigDecimal)query.getSingleResult();
		return result;
	}
	
	@PreDestroy
	public void onDestroy() {		
	}
}
