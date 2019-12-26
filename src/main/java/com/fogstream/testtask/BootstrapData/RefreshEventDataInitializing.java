package com.fogstream.testtask.BootstrapData;

import com.fogstream.testtask.model.Image;
import com.fogstream.testtask.model.News;
import com.fogstream.testtask.model.NewsCategory;
import com.fogstream.testtask.model.User;
import com.fogstream.testtask.repository.ImageRepository;
import com.fogstream.testtask.repository.NewsCategoryRepository;
import com.fogstream.testtask.repository.NewsRepository;
import com.fogstream.testtask.repository.UserRepository;
import com.fogstream.testtask.service.ImageService;
import org.apache.commons.codec.binary.Base64;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


import java.io.File;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Calendar;

@Component
public class RefreshEventDataInitializing implements ApplicationListener<ContextRefreshedEvent>
{
	private UserRepository userRepository;
	private NewsRepository newsRepository;
	private NewsCategoryRepository newsCategoryRepository;
	private PasswordEncoder passwordEncoder;
	private ImageRepository imageRepository;
	private ImageService imageService;

	public RefreshEventDataInitializing(UserRepository userRepository, NewsRepository newsRepository,
										NewsCategoryRepository newsCategoryRepository,
										PasswordEncoder passwordEncoder,
										ImageRepository imageRepository, ImageService imageService)
	{
		this.userRepository = userRepository;
		this.newsRepository = newsRepository;
		this.newsCategoryRepository = newsCategoryRepository;
		this.passwordEncoder = passwordEncoder;
		this.imageRepository = imageRepository;
		this.imageService = imageService;
	}

	@Override
	public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent)
	{
		NewsCategory newsCategorySport = new NewsCategory();
		newsCategorySport.setName("Sport");
		newsCategoryRepository.save(newsCategorySport);
		NewsCategory newsCategoryAuto = new NewsCategory();
		newsCategoryAuto.setName("Auto");
		newsCategoryRepository.save(newsCategoryAuto);
		NewsCategory newsCategoryWeather = new NewsCategory();
		newsCategoryWeather.setName("Weather");
		newsCategoryRepository.save(newsCategoryWeather);

		Image profileImage = null;
		Image autoCategoryImage = null;
		Image sportCategoryImage = null;
		Image weatherCategoryImage = null;

		try
		{
			File profileImageFile = new ClassPathResource("profileImage.jpg").getFile();
			File autoCategoryImageFile = new ClassPathResource("autoCategory.jpg").getFile();
			File sportCategoryImageFile = new ClassPathResource("sportCategory.jpg").getFile();
			File weatherCategoryImageFile = new ClassPathResource("weatherCategory.jpg").getFile();

			profileImage = imageService.createImageFrom64BaseString(
					Base64.encodeBase64String(Files.readAllBytes(profileImageFile.toPath())), "profileImage.jpg");
			autoCategoryImage = imageService.createImageFrom64BaseString(
					Base64.encodeBase64String(Files.readAllBytes(autoCategoryImageFile.toPath())), "autoCategory.jpg");
			sportCategoryImage = imageService.createImageFrom64BaseString(
					Base64.encodeBase64String(Files.readAllBytes(sportCategoryImageFile.toPath())), "sportCategory.jpg");
			weatherCategoryImage = imageService.createImageFrom64BaseString(
					Base64.encodeBase64String(Files.readAllBytes(weatherCategoryImageFile.toPath())), "weatherCategory.jpg");
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}

		User adminUser = new User();
		adminUser.setLogin("log");
		adminUser.setPassword(passwordEncoder.encode("pass"));
		adminUser.setFirstName("Fadmin");
		adminUser.setLastName("Ladmin");
		adminUser.setEmail("email@email.com");
		adminUser.setNewsCategories(Arrays.asList(newsCategorySport, newsCategoryAuto));
		Calendar birthday = Calendar.getInstance();
		birthday.set(1994, Calendar.OCTOBER, 11);
		adminUser.setBirthDay(birthday);
		adminUser.setProfileImage(profileImage);
		adminUser.setGender("Male");
		adminUser.setRegistrationDate(Calendar.getInstance());
		userRepository.save(adminUser);

		for (int i=0; i< 35; ++i)
		{
			News news = new News();
			news.setTitle("News Title " + (i + 1));
			news.setText("News Text Number " + (i + 1) + ".");
			news.setCreatedBy(adminUser);
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.DAY_OF_YEAR, i);
			news.setDateCreated(calendar);
			switch (i % 3)
			{
				case (0) : news.setNewsCategory(newsCategorySport);news.setPreview(sportCategoryImage);break;
				case (1) : news.setNewsCategory(newsCategoryAuto);news.setPreview(autoCategoryImage);break;
				case (2) : news.setNewsCategory(newsCategoryWeather);news.setPreview(weatherCategoryImage);break;
			}
			newsRepository.save(news);
		}
	}
}
