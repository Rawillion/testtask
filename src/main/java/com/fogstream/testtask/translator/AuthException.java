package com.fogstream.testtask.translator;

import lombok.Data;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Data
public class AuthException extends UsernameNotFoundException
{
	private int code;

	public AuthException(int code, String message) {
		super(message);
		this.code = code;
	}
}
