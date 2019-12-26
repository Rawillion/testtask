package com.fogstream.testtask.service;

import com.fogstream.testtask.model.Captcha.GoogleResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.util.regex.Pattern;

@Service
public class CaptchaService
{
	@Value("${captcha.secret-key}")
	String siteSecret;

	private static Pattern RESPONSE_PATTERN = Pattern.compile("[A-Za-z0-9_-]+");

	public void captchaCheck(String token, HttpServletRequest request) throws Exception
	{
		if (token == null || token.isEmpty())
			throw new Exception("Captcha is absent");
		if (!responseSanityCheck(token)) {
			throw new Exception("Response contains invalid characters");
		}

		final URI verifyUri = URI.create(String.format("https://www.google.com/recaptcha/api/siteverify?secret=%s&response=%s&remoteip=%s", siteSecret, token, request.getRemoteAddr()));
		try {
			RestOperations restTemplate = new RestTemplate();
			final GoogleResponse googleResponse = restTemplate.getForObject(verifyUri, GoogleResponse.class);
			System.err.println(googleResponse.toString());

			if (!googleResponse.isSuccess()) {
				throw new Exception("reCaptcha was not successfully validated");
			}
		} catch (RestClientException rce) {
			throw new Exception("Registration unavailable at this time.  Please try again later.", rce);
		}
	}

	private boolean responseSanityCheck(final String response)
	{
		return RESPONSE_PATTERN.matcher(response).matches();
	}
}
