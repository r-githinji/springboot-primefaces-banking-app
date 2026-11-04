package com.jebus.webclient;

import org.springframework.boot.web.servlet.FilterRegistration;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
@FilterRegistration(
		order = Ordered.LOWEST_PRECEDENCE - 3, 
		urlPatterns = "/auth/*"
	)
public class JebusAuthFilter extends JebusCoreFilter {

	@PostConstruct
    public void onCreate() {
        String[][] mappings = new String[][]{
            {"/signin", "signin.xhtml"},
            {"/signup", "signup.xhtml"},
        };
        for (String[] array : mappings) {
        	addMapping(String.format("/auth%s", array[0]), String.format("/WEB-INF/auth/%s", array[1]));
        }
    }

    @PreDestroy
    public void onDestroy() {
    }
}
