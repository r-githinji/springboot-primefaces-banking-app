package com.jebus.webclient.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Past;

public abstract class Funds {

	private Coin amount;
	private LocalDateTime created;
	
	public Funds() {
		created = LocalDateTime.now();
	}

	@NotNull
	@Positive
	public Coin getAmount() {
		return amount;
	}
	
	public void setAmount(Coin amount) {
		this.amount = amount;
	}
	
	@Past
	@NotNull
	public LocalDateTime getCreated() {
		return created;
	}
	
	public void setCreated(LocalDateTime created) {
		this.created = created;
	}
}
