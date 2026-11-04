package com.jebus.webclient.dto;

import com.jebus.webclient.model.Customer;

import jakarta.validation.constraints.NotNull;

public class WithdrawFund extends Funds {

	private Customer source;

	@NotNull(message = "{withdraw.source.isnull}")
	public Customer getSource() {
		return source;
	}

	public void setSource(Customer source) {
		this.source = source;
	}
	
	public static synchronized Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private WithdrawFund instance;
		
		private Builder() {
			instance = new WithdrawFund();
		}					
		
		public Builder source(Customer source) {
			instance.setSource(source);
			return this;
		}
		
		public Builder amount(Coin amount) {
			instance.setAmount(amount);
			return this;
		}
		
		public WithdrawFund build() {
			return instance;
		}
	}
}
