package com.fogstream.testtask.configuration;

import com.fogstream.testtask.filter.CaptchaCheckFilter;
import com.fogstream.testtask.service.CustomUserDetailsService;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter
{
	private CustomUserDetailsService customUserDetailsService;
	private PasswordEncoder passwordEncoder;

	public WebSecurityConfiguration(CustomUserDetailsService customUserDetailsService,
									PasswordEncoder passwordEncoder)
	{
		this.customUserDetailsService = customUserDetailsService;
		this.passwordEncoder = passwordEncoder;
	}

	@Bean
	public AuthenticationManager customAuthenticationManager() throws Exception {
		return authenticationManager();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception
	{
		http.authorizeRequests()
			.antMatchers("/editprofile", "/resources/**", "/", "/login", "/newslist", "/newsCategories", "/vieweditnews").permitAll()
			.antMatchers(HttpMethod.POST, "/user").permitAll()
			.anyRequest().authenticated()
			.and()
			.logout().permitAll()
			.and().httpBasic()
			.and().csrf().disable();
	}

}
