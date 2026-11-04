package com.jebus.webclient;

import jakarta.faces.convert.FacesConverter;
import jakarta.faces.convert.Converter;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;

import com.jebus.webclient.model.Customer;
import com.jebus.webclient.utils.JebusUtils;
import com.jebus.webclient.controller.ApplicationBean;

public abstract class JsfConverters {

	@FacesConverter(value = "jebus.converter.customer")
	public static class CustomerConverter implements Converter<Customer> {

		@Override
		public Customer getAsObject(FacesContext ctx, UIComponent comp, String value) {
			if (null == value || value.isEmpty()) {
				return null;
			}        
			ApplicationBean appBean = JebusUtils.evaluateVE(ctx, 
				String.format("#{%s}", Constants.APPLICATION_BEAN), ApplicationBean.class);
			return appBean.findCustById(Integer.valueOf(value));
		}

		@Override
		public String getAsString(FacesContext ctx, UIComponent comp, Customer cus) {
			if (null == cus) {
				return null;
			}
			return String.valueOf(cus.getId());
		}
	}

}
