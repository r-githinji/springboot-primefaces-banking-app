package com.jebus.webclient.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

import com.jebus.webclient.Constants;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityResult;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Cacheable(false)
@Entity
@Table(name = "entry")
@SqlResultSetMapping(
	name = Constants.ENTRY_META_RESULT, 
	entities = {
		@EntityResult(entityClass = Entry.class)
	},
	columns = {
		@ColumnResult(type = BigDecimal.class, name = "entry_bal")
	}
)
public class Entry implements Serializable {

	private static final long serialVersionUID = 692302378L;
	private Integer id, sequence;
	private Account account;
	private BigDecimal amount = BigDecimal.ZERO, balance = BigDecimal.ZERO;
	private Journal journal;
	private LocalDateTime created;
	private EntryType type; 
	
	public Entry() {
	}
	
	private Entry(EntryType type, Account account, BigDecimal amount) {
		this.type = type;
		this.account = account;
		this.amount = amount;
		created = LocalDateTime.now();
	}  

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "entry_id")
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@NotNull
	@Positive
	@Column(name = "entry_seq", nullable = false, updatable = false)
	public Integer getSequence() {
		return sequence;
	}
	
	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "entry_acc")
	public Account getAccount() {
		return account;
	}
	
	public void setAccount(Account account) {
		this.account = account;
	}
	
	@NotNull
	@Column(name = "entry_amt", updatable = false, nullable = false)
	public BigDecimal getAmount() {       
		return amount;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	@Transient
	public BigDecimal getBalance() {
		return balance;
	}
	
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	
	@NotNull
	@ManyToOne
	@JoinColumn(name = "entry_jnl")
	public Journal getJournal() {
		return journal;
	}
	
	public void setJournal(Journal journal) {
		this.journal = journal;
	}
	
	@NotNull
	@Column(name = "entry_created", nullable = false, updatable = false)
	public LocalDateTime getCreated() {
		return created;
	}
	
	public void setCreated(LocalDateTime created) {
		this.created = created;
	}
	
	@NotNull
	@Column(name = "entry_type", updatable = false, nullable = false)
	public EntryType getType() {
		return type;
	}
	
	public void setType(EntryType type) {
		this.type = type;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(type, account);
	}	

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		} else if (null == other || !(other instanceof Entry)) {
			return false;
		} 
		Entry that = (Entry)other;
		return type == that.getType() && account.equals(that.getAccount());
	}

	private static Entry of(EntryType type, Account account, BigDecimal amount) {
		BigDecimal sign;
		switch (type) {
			case DEBIT:
				switch(account.getType()) {
					case ASSET:
					case EXPENSE:
						sign = BigDecimal.ONE;
					break;
					default:
						sign = BigDecimal.ONE.negate();
				}
			break;
			case CREDIT:
				switch(account.getType()) {
					case ASSET:
					case EXPENSE:
						sign = BigDecimal.ONE.negate();
					break;
					default:
						sign = BigDecimal.ONE;
				}
			break;
			default:
				sign = BigDecimal.ZERO;
		}
		return new Entry(type, account, amount.multiply(sign));
	}

	public static synchronized Entry debit(BigDecimal amount, Account account) {
		return of(EntryType.DEBIT, account, amount);
	}

	public static synchronized Entry credit(BigDecimal amount, Account account) {
		return of(EntryType.CREDIT, account, amount);
	}
	
	public enum EntryType {

		DEBIT("Debit", "DR"), 
		CREDIT("Credit", "CR");
		private final String label, value;

		private EntryType(String label, String value) {
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
