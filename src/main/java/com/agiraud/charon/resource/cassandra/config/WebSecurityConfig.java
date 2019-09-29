package com.agiraud.charon.resource.cassandra.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	/* ************************************************************************* */
	// OVERRIDE
	/* ************************************************************************* */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
        http
	        .formLogin()
	        .loginPage("/login")
	        .failureUrl("/login-error")
	    .and()
	        .logout()
	        .logoutSuccessUrl("/index")
	    /*.and()
	        .authorizeRequests()
            .antMatchers("/user/**").hasRole("USER")*/
	    .and()
	        .exceptionHandling()
	        .accessDeniedPage("/403");
	}  

}
