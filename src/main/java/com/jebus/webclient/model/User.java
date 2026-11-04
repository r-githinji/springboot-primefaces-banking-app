package com.jebus.webclient.model;

import java.util.Objects;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Transient;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Cacheable
@Entity
@Table(name = "usr")
public class User implements UserDetails {

	private static final long serialVersionUID = 88931244L;
	private Integer id;
	private String name, username, password;
	private List<Authority> roles;
	private boolean enabled;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "usr_id")
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}

	@NotEmpty(message = "{user.fullname.isempty}")
	@Column(name = "usr_full_name", updatable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@NotEmpty(message = "{user.name.isempty}")
	@Column(name = "usr_name", updatable = false)
	@Override
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	@NotEmpty(message = "{user.pass.isempty}")
	@Column(name = "usr_pass", updatable = false)
	@Override
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}	
	
	@Size(min = 1, message = "{user.auths.min}")
	@OneToMany(mappedBy = "user", cascade = {CascadeType.ALL})
	public List<Authority> getRoles() {
		return roles;
	}
	
	public void setRoles(List<Authority> roles) {
		this.roles = roles;
	}
	
	@Column(name = "usr_enabled", updatable = false)
	public boolean isEnabled() {
		return enabled;
	}
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	
	@Transient
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		if (null == roles) {
			return List.of();
		}
		return roles.stream().map((role) -> new SimpleGrantedAuthority(role.getType().getValue())).toList();
	}
	
	@Transient
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	@Transient
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	@Transient
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	public void add(Authority auth) {
		if (null == auth) {
			return;
		}
		if (null == roles) {
			roles = new LinkedList<>();
		}
		if (null == auth.getId()) {
			auth.setUser(this);
		}
		if (null == roles.stream().filter(auth::equals).findFirst().orElse(null)) {
			roles.add(auth);
		}
	}
	
	public void remove(Authority auth) {
		if (null == auth || null == roles) {
			return;
		}
		roles.stream().filter(auth::equals).findFirst().ifPresent(roles::remove);
	}
	
	public void clear() {
		if (null == roles) {
			return;
		}
		roles.clear();
	}

	@Override
	public int hashCode() {
		return Objects.hash(username);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		} else if (null == other || !(other instanceof User)) {
			return false;
		}
		User that = User.class.cast(other);
		return Objects.equals(username, that.getUsername());
	}
}
