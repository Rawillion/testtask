package com.fogstream.testtask.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fogstream.testtask.model.NewsCategory;
import com.fogstream.testtask.model.User;
import com.fogstream.testtask.repository.UserRepository;
import com.fogstream.testtask.service.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Any;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NewsCategoriesControllerTest
{
	@Mock
	Principal principal;

	@Mock
	UserRepository userRepository;

	@InjectMocks
	NewsCategoriesController newsCategoriesController;

	MockMvc mockMvc;

	@BeforeEach
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		mockMvc = MockMvcBuilders.standaloneSetup(newsCategoriesController).build();
	}

	@Test
	void getCurrentCategories() throws Exception
	{
		User testUser = new User();
		testUser.setLogin("login");

		NewsCategory newsCategorySport = new NewsCategory();
		newsCategorySport.setId(1L);
		newsCategorySport.setName("Sport");
		NewsCategory newsCategoryAuto = new NewsCategory();
		newsCategoryAuto.setId(2L);
		newsCategoryAuto.setName("Auto");
		NewsCategory newsCategoryWeather = new NewsCategory();
		newsCategoryWeather.setId(3L);
		newsCategoryWeather.setName("Weather");
		List<NewsCategory> categories = Arrays.asList(newsCategorySport, newsCategoryAuto, newsCategoryWeather);

		testUser.setNewsCategories(categories);

		when(principal.getName()).thenReturn(testUser.getLogin());
		when(userRepository.findByLogin(testUser.getLogin())).thenReturn(testUser);

		//categories exist
		MvcResult returnData = mockMvc.perform(get("/currentCategories").principal(principal))
									  .andExpect(status().isOk()).andReturn();
		String resultString = returnData.getResponse().getContentAsString();
		assertEquals(categories, new ObjectMapper().readValue(resultString, new TypeReference<List<NewsCategory>>(){}));

		//there are not categories
		testUser.setNewsCategories(null);
		returnData = mockMvc.perform(get("/currentCategories").principal(principal))
							.andExpect(status().isOk()).andReturn();
		resultString = returnData.getResponse().getContentAsString();
		assertEquals("", resultString);
	}
}