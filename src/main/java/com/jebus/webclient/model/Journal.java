package com.jebus.webclient.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.LinkedList;
import java.util.stream.IntStream;

import java.math.BigDecimal;
import java.math.RoundingMode;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Transient;
import jakarta.persistence.Cacheable;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

@Cacheable(false)
@Entity
@Table(name = "jnl")
public class Journal implements Serializable {

	private static final long serialVersionUID = 74098274641L;
	private Integer id;
	private String reference;
	private LocalDateTime created;
	private List<Entry> entries;
	private JournalType type;
	private JournalSource source;
	
	public Journal() {		
	}
	
	public Journal(String reference, JournalType type, JournalSource source) {
		this.reference = reference;
		this.type = type;
		this.source = source;
		created = LocalDateTime.now();
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "jnl_id")
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	@NotEmpty(message = "{jnl.reference.isempty}")
	@Column(name = "jnl_ref", updatable = false, nullable = false)
	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	@NotNull
	@Column(name = "jnl_created", updatable = false, nullable = false)
	public LocalDateTime getCreated() {
		return created;
	}
	
	public void setCreated(LocalDateTime created) {
		this.created = created;
	}
	
	@OneToMany(mappedBy = "journal", cascade = CascadeType.ALL)
	public List<Entry> getEntries() {
		return entries;
	}
	
	public void setEntries(List<Entry> entries) {
		this.entries = entries;
	}
	
	@NotNull
	@Column(name = "jnl_type", updatable = false, nullable = false)
	public JournalType getType() {
		return type;
	}
	
	public void setType(JournalType type) {
		this.type = type;
	}

	@NotNull
	@Column(name = "jnl_source", updatable = false, nullable = false)
	public JournalSource getSource() {
		return source;
	}

	public void setSource(JournalSource source) {
		this.source = source;
	}

	private void reorder() {
		IntStream.range(0, entries.size()).forEach(index -> entries.get(index).setSequence(index + 1));
	}

	
	public void add(Entry entry) {
		if (null == entry) {
			return;
		} 
		if (null == entries) {
			entries = new LinkedList<>();
		}
		entry.setJournal(this);
		if (entries.stream().filter(entry::equals).count() == 0) {
			entries.add(entry);
		}	
		reorder();
	}
	
	public void remove(Entry entry) {
		if (null == entry || null == entries) {
			return;
		} 
		entries.stream().filter(entry::equals).findFirst().ifPresent(entries::remove);
		reorder();
	}
	
	public void clear() {
		if (null == entries) {
			return;
		}
		entries.clear();
	}
	
	@Transient
	public BigDecimal getDebits() {
		if (null == entries) {
			return BigDecimal.ZERO;
		}
		return entries.stream()
				.filter(entry -> entry.getType() == Entry.EntryType.DEBIT)
				.map(Entry::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add)
				.setScale(3, RoundingMode.HALF_UP);
	}
	
	@Transient
	public BigDecimal getCredits() {
		if (null == entries) {
			return BigDecimal.ZERO;
		}
		return entries.stream()
				.filter(entry -> entry.getType() == Entry.EntryType.CREDIT)
				.map(Entry::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add)
				.setScale(3, RoundingMode.HALF_UP);
	}
	
	@Transient
	public boolean isBalanced() {
		if (null == entries || entries.isEmpty()) {
			return false;
		}
		BigDecimal debits = getDebits();
		BigDecimal credits = getCredits();		
		return debits.add(credits.negate()).compareTo(BigDecimal.ZERO) == 0;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(type, reference);
	}
	
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		} else if (null == other || !(other instanceof Journal)) {
			return false;
		}
		Journal that = (Journal)other;
		return type == that.getType() && Objects.equals(reference, that.getReference());
	}	
	
	public enum JournalType {

		RECEIPT("Cash Receipt", "jnl.REC"), 
		PAYMENT("Payment", "jnl.PAY");
		private final String label, value;

		private JournalType(String label, String value) {
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
	
	public enum JournalSource {
	
		WITHDRAWAL("Withdwrawal", "src.WDW"), 
		DEPOSIT("Deposit", "src.DEP"), 
		TRANSFER("Transfer", "src.TRF");
		
		private final String label, value;

		private JournalSource(String label, String value) {
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
