package com.jebus.webclient.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

import com.jebus.webclient.Constants;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityResult;
import jakarta.persistence.Table;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Transient;

@Entity
@Table(name = "cus")
@Cacheable(false)
@SqlResultSetMapping(
	name = Constants.CUS_META_RESULT, 
	entities = {
		@EntityResult(entityClass = Customer.class)
	},
	columns = {
		@ColumnResult(type = BigDecimal.class, name = "acc_dep"),
		@ColumnResult(type = BigDecimal.class, name = "acc_wdw"),
		@ColumnResult(type = BigDecimal.class, name = "acc_bal")
	}
)
public class Customer {

	private Integer id;
	private LocalDate birthDate;
	private String reference, firstName, lastName, phoneNumber, email;
	private BigDecimal deposits = BigDecimal.ZERO, withdrawals = BigDecimal.ZERO, balance = BigDecimal.ZERO;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cus_id")
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@NotNull
	@Past
	@Column(name = "cus_dob")
	public LocalDate getBirthDate() {
		return birthDate;
	}
	
	public void setBirthDate(LocalDate birthDate) {
		this.birthDate = birthDate;
	}		
	
	@NotNull
	@NotEmpty
	@Column(name = "cus_ref")
	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}
	
	@Transient
	public String getName() {
		return String.format("%s %s", firstName, lastName);
	}

	public void setName(String name) {
	}

	@NotNull
	@NotEmpty
	@Column(name = "cus_fist_name")
	public String getFirstName() {
		return firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	@NotNull
	@NotEmpty
	@Column(name = "cus_last_name")
	public String getLastName() {
		return lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	@NotNull
	@NotEmpty
	@Pattern(regexp = "^(254|0)([0-9]{9})$")
	@Column(name = "cus_phone")
	public String getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	@NotNull
	@NotEmpty
	@Email
	@Column(name = "cus_email")
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	@NotNull
	@Transient
	public BigDecimal getDeposits() {
		return deposits;
	}

	public void setDeposits(BigDecimal deposits) {
		this.deposits = deposits;
	}
	
	@NotNull
	@Transient
	public BigDecimal getWithdrawals() {
		return withdrawals;
	}

	public void setWithdrawals(BigDecimal withdrawals) {
		this.withdrawals = withdrawals;
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
	
	@PrePersist
	public void onPersist() {
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (Objects.isNull(other)) {
			return false;
		}
		Customer customer = (Customer)other;
		return reference.contentEquals(customer.getReference());
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(reference);
	}
}
