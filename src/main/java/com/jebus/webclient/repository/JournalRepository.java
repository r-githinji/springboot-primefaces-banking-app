package com.jebus.webclient.repository;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.CriteriaDelete;

import java.time.LocalDateTime;

import com.jebus.webclient.model.Journal;
import com.jebus.webclient.utils.JebusUtils;

@Repository
public class JournalRepository {

	@Autowired
	private EntityManager em;
	
	public String series() {
		LocalDateTime today = LocalDateTime.now();
		return String.format("JNL/%s/%s", JebusUtils.generate(6), today.getYear());
	}
	
	public Journal create(Journal journal) {
		return em.merge(journal);
	}
	
	public Journal findById(Integer id) {
		return em.find(Journal.class, id);
	}
	
	public String delete(Journal journal) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<Journal> cd = cb.createCriteriaDelete(Journal.class);
		Root<Journal> jnl = cd.from(Journal.class);
		Predicate[] preds = new Predicate[] {cb.equal(jnl.get("id"), journal.getId())};
		cd = cd.where(preds);
		int count = em.createQuery(cd).executeUpdate();
		if (count == 0) {
			return String.format("Journal %s not found!", journal.getId());
		}
		return String.format("Journal %s deleted succesfully!", journal.getId());
	}
} 