package com.jebus.webclient.config;

import static org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher.pathPattern;
import static org.springframework.security.config.Customizer.withDefaults;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.jebus.webclient.utils.JebusUtils;
import com.jebus.webclient.service.UserService;
import com.jebus.webclient.model.User;
import com.jebus.webclient.model.Authority;

@Configuration
public class JebusSecurityConfig {

	@Bean
	public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {

            @Override
            public String encode(CharSequence rawPassword) {
                return JebusUtils.hash(rawPassword.toString());
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                String hash = encode(rawPassword);
                return hash.contentEquals(encodedPassword);
            }
        };
	}
	
	@Bean
	public UserDetailsService userDetailsService(UserService userSvc) {
		return new UserDetailsService() {
			
			@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				User user = userSvc.findByName(username);
				if (null == user) {
					throw new UsernameNotFoundException(String.format("User %s not found!", username));
				}
				return user;
			}			
		};
	}
	
	@Bean
    public SecurityFilterChain formConfig(HttpSecurity http) throws Exception {
        String login = "/auth/signin";
        String roles[] = List.of(Authority.AuthorityType.MANAGER, Authority.AuthorityType.TELLER).stream().map(Authority.AuthorityType::getValue).toArray(String[]::new);		
        return http
            .authorizeHttpRequests(requests -> requests.requestMatchers(pathPattern("/bank/**")).hasAnyAuthority(roles).anyRequest().permitAll())
            .logout(user -> user.logoutSuccessUrl(login))
            .csrf(csrf -> csrf.disable())
            .headers(headers -> 
            	headers
            		.frameOptions(options -> options.sameOrigin())
            		.contentTypeOptions(withDefaults()))
            .formLogin((user) ->
				user.usernameParameter("username")
					.passwordParameter("password")
					.loginPage(login)
					.defaultSuccessUrl("/bank/home"))            
            .build();
    }

}
