package com.jebus.webclient.dto;

import com.jebus.webclient.model.Customer;

import jakarta.validation.constraints.NotNull;

public class TransferFund extends Funds {

	private Customer source, target;
	
	public TransferFund() {			
	}

	@NotNull(message = "{transfer.source.isnull}")
	public Customer getSource() {
		return source;
	}

	public void setSource(Customer source) {
		this.source = source;
	}

	@NotNull(message = "{transfer.target.isnull}")
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
		
		private TransferFund instance;
		
		private Builder() {
			instance = new TransferFund();
		}					
		
		public Builder source(Customer source) {
			instance.setSource(source);
			return this;
		}
		
		public Builder target(Customer target) {
			instance.setTarget(target);
			return this;
		}
		
		public Builder amount(Coin amount) {
			instance.setAmount(amount);
			return this;
		}
		
		public TransferFund build() {
			return instance;
		}
	}
}
