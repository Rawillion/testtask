package com.fogstream.testtask.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fogstream.testtask.service.CaptchaService;
import com.fogstream.testtask.service.CustomUserDetailsService;
import com.fogstream.testtask.testingmodel.TestUserDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Answers.CALLS_REAL_METHODS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest
{
	@Mock
	CustomUserDetailsService userDetailsService;

	@Mock(answer = CALLS_REAL_METHODS)
	CaptchaService captchaService;

	@InjectMocks
	UserController userController;

	MockMvc mockMvc;

	@BeforeEach
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		mockMvc = MockMvcBuilders.standaloneSetup(userController).build();

		ReflectionTestUtils.setField(captchaService, "siteSecret", "6LeIxAcTAAAAAGG-vFI1TnRWxMZNFuojJ4WifJWe");
	}

	@Test
	void create() throws Exception
	{
		TestUserDto testUser = new TestUserDto();
		testUser.setLogin("login");
		testUser.setPassword("password");

		//Expected exception without captcha
		Throwable exception = assertThrows(Exception.class, () ->
		{
			mockMvc.perform(post("/user")
									.contentType(MediaType.APPLICATION_JSON)
									.content(new ObjectMapper().writeValueAsString(testUser)))
				   .andExpect(status().isOk()).andDo(print());
		});
		assertThat(exception.getMessage(), containsString("Captcha is absent"));

		//correct post
		testUser.setPassword("password");
		mockMvc.perform(post("/user")
								.header("captchaToken","unusual-token")
								.contentType(MediaType.APPLICATION_JSON)
								.content(new ObjectMapper().writeValueAsString(testUser)))
			   .andExpect(status().isOk());
	}
}