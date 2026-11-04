package com.jebus.webclient.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

import java.util.LinkedList;
import java.util.List;
import java.math.BigDecimal;

import com.jebus.webclient.Constants;
import com.jebus.webclient.model.Account;
import com.jebus.webclient.model.Entry;

@Repository
public class EntryRepository {

	@Autowired
	private EntityManager em;
	
	private Entry toEntry(Object[] cols) {
		Entry entry = (Entry)cols[0];		
		BigDecimal balance = (BigDecimal)cols[1];
		entry.setBalance(balance);		
		return entry;
	}
	
	public List<Entry> list(Account account) {
		String sql = "SELECT a.*, SUM(a.entry_amt) OVER(ORDER BY a.entry_created) AS entry_bal "
				+ "		FROM entry a JOIN acc b ON b.acc_id = a.entry_acc  "
				+ "	WHERE a.entry_acc = ?1 "
				+ " ORDER BY a.entry_created DESC ";
		Query query = em.createNativeQuery(sql.toString(), Constants.ENTRY_META_RESULT);
		query.setParameter(1, account.getId());
		@SuppressWarnings("unchecked")
		List<Object[]> result = query.getResultList();
		result.isEmpty();
		return result.stream().collect(LinkedList::new, (list, array) -> list.add(toEntry(array)), List::addAll);
	}
}
