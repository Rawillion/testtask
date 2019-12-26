package com.fogstream.testtask;

import com.fogstream.testtask.configuration.*;
import com.fogstream.testtask.filter.CaptchaCheckFilter;
import com.fogstream.testtask.model.User;
import com.fogstream.testtask.service.CaptchaService;
import com.fogstream.testtask.service.CustomUserDetailsService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.authentication.NullRememberMeServices;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {MvcConfiguration.class, CryptSecurityConfiguration.class, WebSecurityConfiguration.class, AuthorizationServerConfiguration.class})
public class OAuthAuthorizationTest
{
	@Autowired
	private WebApplicationContext webAppContext;

	@Autowired
	private FilterChainProxy springSecurityFilterChain;

	private MockMvc mockMvc;

	@Autowired
	PasswordEncoder passwordEncoder;

	@MockBean
	CustomUserDetailsService userDetailsService;

	@MockBean(answer = Answers.CALLS_REAL_METHODS)
	CaptchaCheckFilter captchaCheckFilter;

	@Mock(answer = Answers.CALLS_REAL_METHODS)
	CaptchaService captchaService;

	@BeforeEach
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		ReflectionTestUtils.setField(captchaCheckFilter, "processUrl", "/oauth/token");
		ReflectionTestUtils.setField(captchaCheckFilter, "captchaService", captchaService);
		ReflectionTestUtils.setField(captchaCheckFilter, "logger", LogFactory.getLog(CaptchaCheckFilter.class));
		ReflectionTestUtils.setField(captchaCheckFilter, "rememberMeServices", new NullRememberMeServices());
		ReflectionTestUtils.setField(captchaCheckFilter, "failureHandler", new SimpleUrlAuthenticationFailureHandler());
		captchaCheckFilter.setAuthenticationFailureHandler(new SimpleUrlAuthenticationFailureHandler((String)ReflectionTestUtils.getField(captchaCheckFilter, "processUrl")));

		mockMvc = MockMvcBuilders.webAppContextSetup(this.webAppContext).addFilter(springSecurityFilterChain).build();
	}

	@Test
	public void TestGettingAccessToken() throws Exception
	{
		User testUser = new User();
		testUser.setLogin("login");
		testUser.setPassword(passwordEncoder.encode("password"));

		when(userDetailsService.loadUserByUsername(testUser.getLogin())).thenReturn(testUser);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "password");
		params.add("username", "login");
		params.add("password", "password");
		params.add("scope", "openid");

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(HttpHeaders.AUTHORIZATION, "Basic " + Base64.encodeBase64String(("fogstream" + ":" + "testtask").getBytes()));

		JacksonJsonParser jsonParser = new JacksonJsonParser();

		//empty captcha
		MvcResult returnData = mockMvc.perform(post("/oauth/token")
													  .params(params)
													  .contentType(MediaType.APPLICATION_JSON)
													  .headers(httpHeaders)).andExpect(status().isUnauthorized()).andReturn();
		String resultString = returnData.getResponse().getContentAsString();
		assertEquals("Captcha is absent", jsonParser.parseMap(resultString).get("message").toString());

		//wrong captcha
		httpHeaders.add("captchaToken","unusual-token");
		returnData = mockMvc.perform(post("/oauth/token")
														 .params(params)
														 .contentType(MediaType.APPLICATION_JSON)
														 .headers(httpHeaders)).andExpect(status().isUnauthorized()).andReturn();

		resultString = returnData.getResponse().getContentAsString();
		assertEquals("reCaptcha was not successfully validated", jsonParser.parseMap(resultString).get("message").toString());

		ReflectionTestUtils.setField(captchaService, "siteSecret", "6LeIxAcTAAAAAGG-vFI1TnRWxMZNFuojJ4WifJWe");

		//correct credentials
		httpHeaders.add("captchaToken","unusual-token");
		returnData = mockMvc.perform(post("/oauth/token")
													   .params(params)
													   .contentType(MediaType.APPLICATION_JSON)
													   .headers(httpHeaders))//.andDo(print());
									  .andExpect(status().isOk()).andReturn();

		resultString = returnData.getResponse().getContentAsString();
		assertThat(!jsonParser.parseMap(resultString).get("access_token").toString().isEmpty());
		System.out.println(jsonParser.parseMap(resultString).get("access_token").toString());

		//bad credentials
		testUser.setPassword(testUser.getPassword() + "12345");
		returnData = mockMvc.perform(post("/oauth/token")
												.params(params)
												.contentType(MediaType.APPLICATION_JSON)
												.headers(httpHeaders))
						.andExpect(status().isUnauthorized()).andReturn();
		resultString = returnData.getResponse().getContentAsString();
		assertEquals("Bad credentials", jsonParser.parseMap(resultString).get("message").toString());
	}
}
