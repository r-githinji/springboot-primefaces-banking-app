package com.jebus.webclient.model;

import java.io.Serializable;
import java.util.Objects;
import java.math.BigDecimal;

import com.jebus.webclient.Constants;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Transient;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.EntityResult;
import jakarta.persistence.ColumnResult;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Cacheable(false)
@Entity
@Table(name = "acc")
@SqlResultSetMapping(
	name = Constants.ACC_META_RESULT, 
	entities = {
		@EntityResult(entityClass = Account.class)
	},
	columns = {
		@ColumnResult(type = String.class, name = "acc_desc"),
		@ColumnResult(type = BigDecimal.class, name = "acc_bal"),
		@ColumnResult(type = BigDecimal.class, name = "acc_mvt")
	}
)
public class Account implements Serializable {

	private static final long serialVersionUID = 26722378934900L;
	private Integer id;
	private String number, description;
	private BigDecimal balance, movement;
	private LocalDateTime created;
	private AccountType type;
	private Customer owner;

	public Account() {
	}

	public Account(String number, String description) {
		this.number = number;
		this.description = description;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "acc_id")
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@NotEmpty(message = "{account.number.isempty}")
	@Column(name = "acc_no", nullable = false, updatable = false)
	public String getNumber() {
		return number;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}
	
	@NotEmpty(message = "{account.description.isempty}")
	@Transient
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	@NotNull
	@Column(name = "acc_created", nullable = false, updatable = false)
	public LocalDateTime getCreated() {
		return created;
	}
	
	public void setCreated(LocalDateTime created) {
		this.created = created;
	}
	
	@NotNull
	@Column(name = "acc_type", nullable = false, updatable = false)
	public AccountType getType() {
		return type;
	}
	
	public void setType(AccountType type) {
		this.type = type;
	}
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "acc_owner", nullable = false, updatable = false)
	public Customer getOwner() {
		return owner;
	}
	
	public void setOwner(Customer owner) {
		this.owner = owner;
	}
	
	@NotNull
	@PositiveOrZero
	@Transient
	public BigDecimal getBalance() {
		return balance;
	}
	
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	
	@NotNull
	@PositiveOrZero
	@Transient
	public BigDecimal getMovement() {
		return movement;
	}
	
	public void setMovement(BigDecimal movement) {
		this.movement = movement;
	}
    
	@Override
	public int hashCode() {
		return Objects.hash(number, owner);
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		} else if (null == other || !Account.class.isInstance(other)) {
			return false;
		} 
		Account that = Account.class.cast(other);
		return Objects.equals(number, that.getNumber()) && Objects.equals(owner, that.getOwner());
	}
	
	public enum AccountType {
	    
		ASSET("Asset", "ASSET"), 
		EXPENSE("Expense", "EXPENSE"), 
		LIABILITY("Liability", "LIABILITY"), 
		EQUITY("Equity", "EQUITY"), 
		REVENUE("Revenue", "REVENUE");
		private final String label, value;

		private AccountType(String label, String value) {
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
