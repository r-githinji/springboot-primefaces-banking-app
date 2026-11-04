package com.jebus.webclient;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JebusCoreFilter extends OncePerRequestFilter {

	private Map<String, String> mappings;
    
    protected void addMapping(String key, String value) {
        if (null == mappings) {
            mappings = new ConcurrentHashMap<>();
        }
        mappings.put(key, value);
    }

    protected RequestDispatcher dispatcher(HttpServletRequest request, String uri) {
        RequestDispatcher dispatcher = null;
        for (Map.Entry<String, String> entry : mappings.entrySet()) {
            UriTemplate template = new UriTemplate(entry.getKey());
            if (template.matches(uri)) {
                Map<String, String> variables = template.match(uri);
                MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
                for (Map.Entry<String, String> kv : variables.entrySet()) {
                    params.add(kv.getKey(), kv.getValue());
                }
                UriComponentsBuilder uriBuilder = UriComponentsBuilder
                        .fromUriString(entry.getValue()).queryParams(params);
                dispatcher = request.getRequestDispatcher(uriBuilder.toUriString());
                break;
            }
        }
        return dispatcher;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain chain) throws ServletException, IOException {    	
        String uri = request.getRequestURI();
        //check request URI routing path in mappings
        RequestDispatcher dispatcher = dispatcher(request, uri);
        if (null == dispatcher) {
            chain.doFilter(request, response);
        } else {
            JebusViewHandler.action.set(uri);
            try {
            	dispatcher.forward(request, response);
            } finally {
            	JebusViewHandler.action.remove();
            }
        }
    }
}
