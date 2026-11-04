package com.jebus.webclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@SpringBootApplication
public class JebusBankApplication {
	
	@PersistenceContext
	private EntityManager em;

	public static void main(String[] args) {
		SpringApplication.run(JebusBankApplication.class, args);
	}
	
	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource ms = new ReloadableResourceBundleMessageSource();
		ms.setDefaultEncoding("UTF-8");
		ms.setBasename("classpath:ValidationMessages");
		return ms;
	}
	
	@Bean
	public LocalValidatorFactoryBean validatorFactory() {
		LocalValidatorFactoryBean lvfb = new LocalValidatorFactoryBean();
		lvfb.setValidationMessageSource(messageSource());
		return lvfb;
	}
	
	@Bean
	public EntityManager jebusEntityManager() {
		return em;
	}
}
