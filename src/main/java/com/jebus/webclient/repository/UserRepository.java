package com.jebus.webclient.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Predicate;

import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;

import com.jebus.webclient.model.User;

@Repository
public class UserRepository {

	@Autowired
	private EntityManager em;
	
	public User create(User user) {
		return em.merge(user);
	}
	
	public User findById(Integer id) {
		return em.find(User.class, id);
	}
	
	public User findByName(String name) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<User> cq = cb.createQuery(User.class);
		Root<User> usr = cq.from(User.class);
		Predicate[] preds = new Predicate[] {cb.equal(usr.get("username"), name)};
		cq = cq.select(usr).where(preds);
		return em.createQuery(cq).getResultList().stream().findFirst().orElse(null);
	}
	
	public String delete(User user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaDelete<User> cd = cb.createCriteriaDelete(User.class);
		Root<User> usr = cd.from(User.class);
		Predicate[] filter = new Predicate[] {cb.equal(usr.get("id"), user.getId())};
		cd = cd.where(filter);
		int rows = em.createQuery(cd).executeUpdate();
		if (rows == 1) {
			return String.format("User %s deleted succesfully!", user.getUsername());
		}
		throw new IllegalStateException("User delete failed");
	}
}
