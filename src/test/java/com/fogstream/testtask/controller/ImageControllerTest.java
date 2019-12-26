package com.fogstream.testtask.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fogstream.testtask.model.Image;
import com.fogstream.testtask.repository.ImageRepository;
import com.fogstream.testtask.service.ImageService;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
class ImageControllerTest
{
	MockMvc mockMvc;

	@Autowired
	ImageRepository imageRepository;

	@Mock(answer = Answers.CALLS_REAL_METHODS)
	ImageService imageService;

	@InjectMocks
	ImageController imageController;

	@Value("${storage.images}")
	private String imageStorage;

	@BeforeEach
	public void setup()
	{
		MockitoAnnotations.initMocks(this);

		mockMvc = MockMvcBuilders.standaloneSetup(imageController).build();

		ReflectionTestUtils.setField(imageService, "imageRepository", imageRepository);
		ReflectionTestUtils.setField(imageService, "imageStorage", imageStorage + "/test");
	}

	@Test
	void getView() throws Exception
	{
		File profileImageFile = new ClassPathResource("profileImage.jpg").getFile();
		Image profileImage = imageService.createImageFrom64BaseString(
				Base64.encodeBase64String(Files.readAllBytes(profileImageFile.toPath())), "profileImage.jpg");
		Long imageId = profileImage.getId();

		//wrong Id
		MvcResult result = mockMvc.perform(get("/image/view/" + (imageId + 1))
												   .contentType(MediaType.APPLICATION_JSON))
								  .andExpect(status().isOk())
								  .andReturn();
		assertEquals("", result.getResponse().getContentAsString());

		//correct Id
		result = mockMvc.perform(get("/image/view/" + imageId)
										 .contentType(MediaType.APPLICATION_JSON))
						.andExpect(status().isOk())
						.andReturn();
		assertEquals(Base64.encodeBase64String(Files.readAllBytes(profileImageFile.toPath())), result.getResponse().getContentAsString());

		FileUtils.deleteDirectory(new File(imageStorage + "/test"));
	}
}