package com.fogstream.testtask.service;

import com.fogstream.testtask.configuration.CryptSecurityConfiguration;
import com.fogstream.testtask.model.User;
import com.fogstream.testtask.repository.UserRepository;
import com.fogstream.testtask.testingmodel.TestUserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Any;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest
{
	@Autowired
	UserRepository userRepository;

	@Mock
	PasswordEncoder passwordEncoder;

	@InjectMocks
	CustomUserDetailsService userDetailsService;

	@BeforeEach
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		ReflectionTestUtils.setField(userDetailsService, "userRepository", userRepository);
	}

	@Test
	void createUser() throws Exception
	{
		User testUser = new User();
		testUser.setLogin("login");
		testUser.setPassword("password");

		//empty password
		testUser.setPassword(null);
		Throwable exception = assertThrows(Exception.class, () ->
		{
			userDetailsService.createUser(testUser);
		});
		assertEquals(exception.getMessage(), "Users with empty passwords are denied");

		when(passwordEncoder.encode(any(String.class))).thenReturn("$2a$10$qy/ftHWIdKnSMP06P.71/eapW5PSJ9R5M1XMoPdeu0Cp0DX5j09V2");//password in BCrypt

		//correct data
		testUser.setPassword("password");
		userDetailsService.createUser(testUser);
		User savedUser = userRepository.findByLogin(testUser.getLogin());
		assertNotNull(savedUser);
		//date format
		SimpleDateFormat parser = new SimpleDateFormat("dd.MM.yyyy");
		assertEquals(parser.format(savedUser.getRegistrationDate().getTime()), parser.format(Calendar.getInstance().getTime()));

		//save with same login
		exception = assertThrows(Exception.class, () ->
		{
			userDetailsService.createUser(testUser);
		});
		assertEquals(exception.getMessage(), "User with same login already exists");
		userRepository.deleteAll();
	}

	@Test
	void loadUserByUsername() throws Exception
	{
		User testUser = new User();
		testUser.setLogin("login");
		testUser.setPassword("password");

		//valid data
		userDetailsService.createUser(testUser);
		User savedUser = userRepository.findByLogin(testUser.getLogin());
		assertNotNull(savedUser);

		//test correct login
		UserDetails loginUser = userDetailsService.loadUserByUsername(savedUser.getLogin());
		assertNotNull(loginUser);

		//test incorrect login
		String incorrectLogin = "login2";
		Throwable exception = assertThrows(UsernameNotFoundException.class, () ->
		{
			userDetailsService.loadUserByUsername(incorrectLogin);
		});
		assertEquals(exception.getMessage(), String.format("User %s does not exist!", incorrectLogin));
		userRepository.deleteAll();
	}
}