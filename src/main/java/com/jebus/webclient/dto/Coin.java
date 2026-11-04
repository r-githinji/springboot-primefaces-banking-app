package com.jebus.webclient.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class Coin {

	private BigDecimal value;
	private String currency = "kes";

	@NotNull(message = "{coin.value.isnull}")
	@Positive(message = "{coin.value.zero}")
	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}
	
	@NotEmpty
	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Long toCents() {
		return (long)(null == value ? BigDecimal.ZERO.longValue() : value.doubleValue() * 100);
	}
	
	public static synchronized Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private Coin instance;
		
		private Builder() {
			instance = new Coin();
		}
		
		public Builder currency(String code) {
			instance.setCurrency(code);
			return this;
		}
		
		public Builder amount(BigDecimal coin) {
			instance.setValue(coin);
			return this;
		}
		
		public Coin build() {
			return instance;
		}
	}

}
