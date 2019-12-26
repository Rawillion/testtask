package com.fogstream.testtask.translator;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;

public class CustomExceptionTranslator implements WebResponseExceptionTranslator
{
	@Override
	public ResponseEntity translate(Exception e)
	{
		if (e instanceof AuthException) {
			return new ResponseEntity<>(new Result(((AuthException) e).getCode(), e.getMessage()), HttpStatus.FORBIDDEN);
		}
		return new ResponseEntity<>(new Result(-1, e.getMessage()), HttpStatus.UNAUTHORIZED);
	}
}
