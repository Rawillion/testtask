package com.fogstream.testtask.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fogstream.testtask.model.Dto.NewsInputDto;
import com.fogstream.testtask.model.Image;
import com.fogstream.testtask.model.News;
import com.fogstream.testtask.model.User;
import com.fogstream.testtask.repository.NewsCategoryRepository;
import com.fogstream.testtask.repository.NewsRepository;
import com.fogstream.testtask.repository.UserRepository;
import com.fogstream.testtask.service.ImageService;
import graphql.schema.DataFetchingEnvironment;
import graphql.servlet.context.DefaultGraphQLServletContext;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.HashMap;

@Component
public class NewsMutationActionResolver implements GraphQLMutationResolver
{
	private NewsRepository newsRepository;
	private UserRepository userRepository;
	private NewsCategoryRepository newsCategoryRepository;
	private ImageService imageService;

	public NewsMutationActionResolver(NewsRepository newsRepository, UserRepository userRepository,
									  NewsCategoryRepository newsCategoryRepository,
									  ImageService imageService)
	{
		this.newsRepository = newsRepository;
		this.userRepository = userRepository;
		this.newsCategoryRepository = newsCategoryRepository;
		this.imageService = imageService;
	}

	@Transactional
	public String updateOrCreateNews(NewsInputDto newsInputDto, DataFetchingEnvironment env) throws Exception
	{
		News news;
		if (newsInputDto.getId() != -1)
			news = newsRepository.getOne(newsInputDto.getId());
		else
		{
			news = new News();
			String userName = ((DefaultGraphQLServletContext)env.getContext()).getHttpServletRequest().getUserPrincipal().getName();
			User user = userRepository.findByLogin(userName);
			news.setCreatedBy(user);
			news.setDateCreated(Calendar.getInstance());
		}

		news.setTitle(newsInputDto.getTitle());
		news.setText(newsInputDto.getText());

		news.setNewsCategory(newsCategoryRepository.getOne(newsInputDto.getNewsCategory()));

		try
		{
			if (!newsInputDto.getImageName().equals(""))
			{
				Image imageForDelete = news.getPreview();
				if (imageForDelete != null && imageForDelete.getProfiles().size() == 0 && imageForDelete.getNews().size() == 1)
				{
					news.setPreview(null);
					newsRepository.save(news);
					imageService.deleteImage(imageForDelete);
				}
				news.setPreview(imageService.createImageFrom64BaseString(newsInputDto.getImage(), newsInputDto.getImageName()));
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return getReturnMessage(e.getMessage());
		}

		newsRepository.save(news);
		return getReturnMessage("Success");
	}

	private String getReturnMessage(String message) throws Exception
	{
		HashMap<String, String> map = new HashMap<>();
		map.put("message", message);
		return new ObjectMapper().writeValueAsString(map);
	}
}
