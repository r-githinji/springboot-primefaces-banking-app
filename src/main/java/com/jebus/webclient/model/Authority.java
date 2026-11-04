package com.jebus.webclient.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;

import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.GrantedAuthority;

@Cacheable
@Entity
@Table(name = "auth")
public class Authority implements GrantedAuthority {

	private static final long serialVersionUID = 3933838922L;
	private Integer id;
	private User user;
	private AuthorityType type;
	
	public Authority() {
	}
	
	public Authority(AuthorityType type) {
		this.type = type;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "auth_id")
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "auth_usr")
	public User getUser() {
		return user;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	@NotNull
	@Column(name = "auth_type", updatable = false)
	public AuthorityType getType() {
		return type;
	}

	public void setType(AuthorityType type) {
		this.type = type;
	}
	
	@Transient
	@Override
	public String getAuthority() {
		return type.name();
	}

	public enum AuthorityType {
		
		MANAGER("Bank Manager", "au.MGR"),
		TELLER("Bank Teller", "au.TLR");
		
		private final String label, value;
		
		private AuthorityType(String label, String value) {
			this.label = label;
			this.value = value;
		}

		public String getLabel() {
			return label;
		}
		
		public String getValue() {
			return value;
		}
	}
}