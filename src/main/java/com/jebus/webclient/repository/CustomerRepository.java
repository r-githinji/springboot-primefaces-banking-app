package com.jebus.webclient.repository;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.math.BigDecimal;

import com.jebus.webclient.model.Customer;
import com.jebus.webclient.model.Journal;
import com.jebus.webclient.utils.JebusUtils;
import com.jebus.webclient.Constants;

@Repository
public class CustomerRepository {
	
	@Autowired
	private EntityManager em;
	
	public String series() {
		LocalDateTime today = LocalDateTime.now();
		return String.format("AC/%s/%s", JebusUtils.generate(6), today.getYear());
	}
	
	public Customer findById(Integer id) {
		return em.find(Customer.class, id);
	}

	public Customer create(Customer customer) {
		return em.merge(customer);
	}
	
	public Customer findByReference(String reference) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Customer> cq = cb.createQuery(Customer.class);
		Root<Customer> cus = cq.from(Customer.class);
		Predicate[] preds = new Predicate[] {cb.equal(cus.get("reference"), reference)};
		cq = cq.where(preds);
		return em.createQuery(cq).getResultList().stream().findFirst().orElse(null);
	}
	
	public int count() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<Customer> cus = cq.from(Customer.class);
		Predicate[] preds = new Predicate[] {cb.notEqual(cus.get("reference"), Constants.JEBUS_BANK_REF)};
		cq = cq.select(cb.count(cus)).where(preds);
		Long count = em.createQuery(cq).getSingleResult();
		return count.intValue();
	}
	
	private Customer toCustomer(Object[] cols) {
		Customer customer = (Customer)cols[0];
		//deposits
		BigDecimal deposits = (BigDecimal)cols[1];
		customer.setDeposits(deposits);
		//withdrawals
		BigDecimal withdrawals = (BigDecimal)cols[2];
		customer.setWithdrawals(withdrawals);
		//balance
		BigDecimal balance = (BigDecimal)cols[3];
		customer.setBalance(balance);
		return customer;
	}
	
	public List<Customer> list(int offset, int limit) {
		String sql = "WITH cte AS ( "
				+ "		SELECT a.cus_id, "
				+ "			SUM(CASE WHEN d.jnl_type = ?1 THEN c.entry_amt ELSE 0 END) AS acc_dep, "
				+ "			SUM(CASE WHEN d.jnl_type = ?2 THEN c.entry_amt ELSE 0 END) AS acc_wdw, "
				+ "			SUM(c.entry_amt) AS acc_bal "
				+ "			FROM cus a "
				+ "			JOIN acc b ON b.acc_owner = a.cus_id "
				+ "			JOIN entry c ON c.entry_acc = b.acc_id "
				+ "			JOIN jnl d ON d.jnl_id = c.entry_jnl "
				+ "    	WHERE b.acc_no = ?3 "
				+ "    	GROUP BY a.cus_id "
				+ "	  ) "
				+ "	  SELECT a.*, COALESCE(b.acc_dep, 0) AS acc_dep, COALESCE(b.acc_wdw, 0) AS acc_wdw, COALESCE(b.acc_bal, 0) AS acc_bal "
				+ "	  	FROM cus a "
				+ "     LEFT JOIN cte b ON b.cus_id = a.cus_id "
				+ "   WHERE a.cus_ref <> ?4 ";
		Query query = em.createNativeQuery(sql.toString(), Constants.CUS_META_RESULT);
		query.setParameter(1, Journal.JournalType.RECEIPT.getValue());
		query.setParameter(2, Journal.JournalType.PAYMENT.getValue());
		query.setParameter(3, "2001");
		query.setParameter(4, Constants.JEBUS_BANK_REF);
		@SuppressWarnings("unchecked")
		List<Object[]> result = query.getResultList();
		return result.stream().collect(LinkedList::new, (list, array) -> list.add(toCustomer(array)), List::addAll);
	}
	
	public String delete(Customer customer) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<Customer> cd = cb.createCriteriaDelete(Customer.class);
		Root<Customer> cus = cd.from(Customer.class);
		Predicate[] filter = new Predicate[] {cb.equal(cus.get("id"), customer.getId())};
		cd = cd.where(filter);
		int rows = em.createQuery(cd).executeUpdate();
		if (rows == 1) {
			return String.format("Customer %s %s deleted successfully", customer.getFirstName(), customer.getLastName());
		}
		throw new IllegalStateException("Customer delete failed");
	}
}
