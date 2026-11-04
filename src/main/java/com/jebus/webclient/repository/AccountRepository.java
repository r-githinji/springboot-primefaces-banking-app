package com.jebus.webclient.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.math.BigDecimal;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import com.jebus.webclient.model.Account;
import com.jebus.webclient.model.Customer;
import com.jebus.webclient.Constants;

@Repository
public class AccountRepository {

	@Autowired
	private EntityManager em;
	
	public int create(Customer customer) {
		String sql = "INSERT INTO acc (acc_no, acc_created, acc_owner, acc_type) "
				+ "   WITH cte AS ( "
				+ "	  	SELECT c.cus_id, v.coa_no, v.coa_type "
				+ "			FROM cus c, vw_coa v "
				+ "	  	WHERE c.cus_id = ?1 "
				+ "   ) "
				+ "   SELECT a.coa_no, NOW(), a.cus_id, a.coa_type "
				+ "	  FROM cte a LEFT JOIN acc b ON b.acc_owner = a.cus_id AND b.acc_no = a.coa_no "
				+ "   WHERE b.acc_id IS NULL ";
		Query query = em.createNativeQuery(sql.toString());
		query.setParameter(1, customer.getId());
		return query.executeUpdate();
	}
	
	private Account toAccount(Object[] cols) {
		Account account = (Account)cols[0];
		//description
		String desc = (String)cols[1];
		account.setDescription(desc);
		//balance
		BigDecimal balance = (BigDecimal)cols[2];
		account.setBalance(balance);
		//movement
		BigDecimal movement = (BigDecimal)cols[3];
		account.setMovement(movement);
		return account;
	}
	
	public List<Account> list(Customer customer) {		
		String sql = "WITH cte AS ("
				+ "		SELECT a.* "
				+ "			FROM acc a "
				+ "        WHERE a.acc_owner = ?1"
				+ "   ), "
				+ "   cte0 AS ( "
				+ "		SELECT a.acc_id, SUM(e.entry_amt) AS alpha_bal, SUM(e.entry_amt) AS omega_bal "
				+ "			FROM cte a LEFT JOIN entry e ON e.entry_acc = a.acc_id "
				+ "        WHERE true "
				+ "        GROUP BY a.acc_id "
				+ "    ) "
				+ "    SELECT a.*, v.coa_desc AS acc_desc, COALESCE(b.omega_bal, 0.0) AS acc_bal, COALESCE(b.omega_bal, 0.0) - COALESCE(b.alpha_bal, 0.0) AS acc_mvt "
				+ "	   FROM cte a LEFT JOIN cte0 b ON b.acc_id = a.acc_id JOIN vw_coa v ON v.coa_no = a.acc_no"
				+ "    WHERE true ";
		Query query = em.createNativeQuery(sql.toString(), Constants.ACC_META_RESULT);
		query.setParameter(1, customer.getId());
		@SuppressWarnings("unchecked")
		List<Object[]> result = query.getResultList();
		return result.stream().collect(LinkedList::new, (list, array) -> list.add(toAccount(array)), List::addAll);
	}
	
	public Account fetch(String number, Customer customer) {
		List<Account> accounts = list(customer);
		if (null == accounts) {
			accounts = List.of();
		}
		Map<String, Account> chart = accounts.stream().collect(Collectors.toMap(Account::getNumber, Function.identity()));
		return chart.get(number);
	}
}
