package com.jebus.webclient;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JsfHttpErrorFilter extends JebusCoreFilter {

	private static final Logger logger =  LoggerFactory.getLogger(JsfHttpErrorFilter.class);
	
	//@Override
	protected boolean shouldNotFilterErrorDispatch() {
		return false;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
          FilterChain chain) throws ServletException, IOException { 
		String uri = request.getRequestURI(), query = request.getQueryString();            
		int status = response.getStatus();
		logger.info("JSF Error URI {} having query {} with status {} ", uri, query, status);            
		switch (status) {
			case HttpServletResponse.SC_NOT_FOUND -> {
				JebusViewHandler.action.set(uri);
				try {
					RequestDispatcher dispatcher = request.getRequestDispatcher(String.format("/WEB-INF/error/%s.xhtml", status));
					dispatcher.forward(request, response);
				} finally {
					JebusViewHandler.action.remove();
				}
				break;
			} 
			default ->
				chain.doFilter(request, response);
		}            
	}


}
