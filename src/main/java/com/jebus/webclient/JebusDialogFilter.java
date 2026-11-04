package com.jebus.webclient;

import org.springframework.boot.web.servlet.FilterRegistration;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Component
@FilterRegistration(
		order = Ordered.LOWEST_PRECEDENCE - 1, 
		urlPatterns = "/dialog/*"
	)
public class JebusDialogFilter extends JebusCoreFilter {

	@PostConstruct
    public void onCreate() {
        String[][] mappings = new String[][]{
            {"/deposit-success", "deposit-complete.xhtml"},
            {"/withdraw-success", "withdrawal-complete.xhtml"},
            {"/transfer-success", "transfer-complete.xhtml"},
        };
        for (String[] array : mappings) {
        	addMapping(String.format("/dialog%s", array[0]), String.format("/WEB-INF/dialog/%s", array[1]));
        }
    }

    @PreDestroy
    public void onDestroy() {
    }
}
