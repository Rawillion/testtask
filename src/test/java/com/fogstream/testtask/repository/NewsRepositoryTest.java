package com.fogstream.testtask.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fogstream.testtask.model.News;
import com.fogstream.testtask.model.NewsCategory;
import com.fogstream.testtask.model.User;
import com.fogstream.testtask.service.CaptchaService;
import org.apache.commons.codec.binary.Base64;
import org.checkerframework.checker.units.qual.A;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@Transactional
class NewsRepositoryTest
{
	@Autowired
	MockMvc mockMvc;

	@Autowired
	NewsRepository newsRepository;

	@Autowired
	NewsCategoryRepository newsCategoryRepository;

	List<News> allNews = new ArrayList<>();

	List<NewsCategory> allCategories = new ArrayList<>();

	@MockBean(answer = Answers.CALLS_REAL_METHODS)
	CaptchaService captchaService;

	String access_token = "";

	@BeforeEach
	void setup() throws Exception
	{
		ReflectionTestUtils.setField(captchaService, "siteSecret", "6LeIxAcTAAAAAGG-vFI1TnRWxMZNFuojJ4WifJWe");

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "password");
		params.add("username", "log");
		params.add("password", "pass");
		params.add("scope", "openid");

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add(HttpHeaders.AUTHORIZATION, "Basic " + Base64.encodeBase64String(("fogstream" + ":" + "testtask").getBytes()));
		httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
		httpHeaders.add("captchaToken","unusual-token");

		ResultActions result = mockMvc.perform(post("/oauth/token")
													   .params(params)
													   .contentType(MediaType.APPLICATION_JSON)
													   .headers(httpHeaders))
									  .andExpect(status().isOk());

		String resultString = result.andReturn().getResponse().getContentAsString();

		JacksonJsonParser jsonParser = new JacksonJsonParser();
		access_token = jsonParser.parseMap(resultString).get("access_token").toString();

		allNews = newsRepository.findAll();
		allCategories = newsCategoryRepository.findAll();
	}

	@Test
	public void testFilteringAndPaging() throws Exception
	{
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.add("Authorization", "Bearer " + access_token);

		List<NewsCategory> testingCategories = allCategories.subList(0, 2);
		String Ids = testingCategories.stream().map(newsCategory -> newsCategory.getId().toString())
									  .reduce((id, idString) -> idString + "," + id).get();

		int pageSize = 7;
		int pageNumber = 1;
		int totalElements = (int) allNews.stream().filter(n -> testingCategories.contains(n.getNewsCategory())).count();

		mockMvc.perform(get("/news/search/findAllByNewsCategories?categories=" + Ids + "&page=" + pageNumber + "&size=" + pageSize)
								.headers(httpHeaders))
			   .andExpect(jsonPath("$.page.totalElements", is(totalElements)))
			   .andExpect(jsonPath("$.page.size", is(pageSize)))
			   .andExpect(jsonPath("$._embedded.news", hasSize(pageSize)))
			   .andExpect(jsonPath("$.page.number", is(pageNumber)))
			   .andExpect(jsonPath("$.page.totalPages", is((totalElements % pageSize) != 0 ? totalElements / pageSize + 1 : totalElements / pageSize)));
	}
}