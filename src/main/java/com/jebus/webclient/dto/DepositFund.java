package com.jebus.webclient.dto;

import com.jebus.webclient.model.Customer;

import jakarta.validation.constraints.NotNull;

public class DepositFund extends Funds {

	private Customer target;

	@NotNull(message = "{deposit.target.isnull}")
	public Customer getTarget() {
		return target;
	}

	public void setTarget(Customer target) {
		this.target = target;
	}
	
	public static synchronized Builder builder() {
		return new Builder();
	}
	
	public static class Builder {
		
		private DepositFund instance;
		
		private Builder() {
			instance = new DepositFund();
		}
		
		public Builder target(Customer target) {
			instance.setTarget(target);
			return this;
		}
		
		public Builder amount(Coin amount) {
			instance.setAmount(amount);
			return this;
		}
		
		public DepositFund build() {
			return instance;
		}
	}
}
