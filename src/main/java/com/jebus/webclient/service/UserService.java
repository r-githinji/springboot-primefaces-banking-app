package com.jebus.webclient.service;

import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import com.jebus.webclient.repository.UserRepository;
import com.jebus.webclient.model.User;
import com.jebus.webclient.model.Authority;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepo;

	public User findById(Integer id) {
		return userRepo.findById(id);
	}
	
	public User findByName(String username) {
		return userRepo.findByName(username);
	}
	
	public User create(Authority.AuthorityType ...auths) {
		User user = new User();
		List<Authority> roles = Stream.of(auths)
			.map(Authority::new).toList();
		roles.forEach(user::add);
		user.setRoles(roles);
		return user;
	}
	
	@Transactional
	public User create(User user) {
		return userRepo.create(user);
	}
	
	@Transactional
	public String delete(User user) {
		return userRepo.delete(user);
	}
}
