package com.jebus.webclient;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

import org.springframework.boot.web.servlet.FilterRegistration;
import org.springframework.core.Ordered;

@Component
@FilterRegistration(
		order = Ordered.LOWEST_PRECEDENCE - 2, 
		urlPatterns = "/bank/*"
	)
public class JebusBankFilter extends JebusCoreFilter {

	@PostConstruct
    public void onCreate() {
        String home = "home.xhtml";
        String[][] mappings = new String[][]{
            {"", home},
            {"/", home},
            {"/home", home},
            {"/profile", "profile.xhtml"},
            {"/account/deposit", "account/deposit.xhtml"},
            {"/account/withdraw", "account/withdraw.xhtml"},
            {"/account/transfer", "account/transfer.xhtml"},
            {"/account/open", "customer/create.xhtml"},
            {"/account/list", "customer/list.xhtml"},
            {"/account/{id}", "customer/view.xhtml"},
        };
        for (String[] array : mappings) {
        	addMapping(String.format("/bank%s", array[0]), String.format("/WEB-INF/bank/%s", array[1]));
        }
    }

    @PreDestroy
    public void onDestroy() {
    }
} 