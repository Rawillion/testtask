package com.fogstream.testtask.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fogstream.testtask.service.CaptchaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@Lazy
public class CaptchaCheckFilter extends AbstractAuthenticationProcessingFilter
{
	private String processUrl;
	private CaptchaService captchaService;

	@Autowired
	@Qualifier("customAuthenticationManager")
	@Override
	public void setAuthenticationManager(AuthenticationManager authenticationManager) {
		super.setAuthenticationManager(authenticationManager);
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		if (processUrl.equals(request.getRequestURI()))
		{
			String captchaToken = request.getHeader("captchaToken");
			try
			{
				captchaService.captchaCheck(captchaToken, request);
			}
			catch (Exception e)
			{
				((HttpServletResponse) res).setHeader("Content-Type", "application/json");
				((HttpServletResponse) res).setStatus(401);
				Map<String, String> errorMessage = new HashMap<>();
				errorMessage.put("message", e.getMessage());
				res.getOutputStream().write(new ObjectMapper().writeValueAsString(errorMessage).getBytes());
				return;
			}
		}
		chain.doFilter(req, res);
	}

	public CaptchaCheckFilter(@Value("/oauth/token") String defaultFilterProcessesUrl,
							  CaptchaService captchaService)
	{
		super(defaultFilterProcessesUrl);
		this.processUrl = defaultFilterProcessesUrl;
		this.captchaService = captchaService;
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws AuthenticationException, IOException, ServletException
	{
		return null;
	}
}
