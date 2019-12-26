package com.fogstream.testtask.service;

import com.fogstream.testtask.model.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;

public class TokenService extends DefaultTokenServices
{
	private UserDetailsService userDetailsService;

	public TokenService(UserDetailsService userDetailsService)
	{
		this.userDetailsService = userDetailsService;
	}

	@Override
	public OAuth2Authentication loadAuthentication(String accessToken) {
		OAuth2Authentication oAuth2Authentication = super.loadAuthentication(accessToken);
		UserDetails user = userDetailsService.loadUserByUsername(String.valueOf(oAuth2Authentication.getPrincipal()));
		((UsernamePasswordAuthenticationToken) oAuth2Authentication.getUserAuthentication()).setDetails(user);
		return oAuth2Authentication;
	}
}
