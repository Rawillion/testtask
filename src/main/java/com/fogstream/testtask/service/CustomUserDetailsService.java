package com.fogstream.testtask.service;

import com.fogstream.testtask.model.User;
import com.fogstream.testtask.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class CustomUserDetailsService implements UserDetailsService
{
	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;

	public CustomUserDetailsService(UserRepository userRepository, PasswordEncoder passwordEncoder)
	{
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException
	{
		User user = userRepository.findByLogin(s);
		if (user == null) {
			throw new UsernameNotFoundException(String.format("User %s does not exist!", s));
		}
		return user;
	}

	public void createUser(User newUser) throws Exception
	{
		if (userRepository.findByLogin(newUser.getLogin()) != null)
			throw new Exception("User with same login already exists");

		if (newUser.getPassword() == null || newUser.getPassword().equals(""))
			throw new Exception("Users with empty passwords are denied");

		newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
		newUser.setRegistrationDate(Calendar.getInstance());
		userRepository.save(newUser);
	}
}
