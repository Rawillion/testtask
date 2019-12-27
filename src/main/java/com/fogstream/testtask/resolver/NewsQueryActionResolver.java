package com.fogstream.testtask.resolver;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.fogstream.testtask.model.Dto.NewsOutputDto;
import com.fogstream.testtask.model.News;
import com.fogstream.testtask.repository.NewsRepository;
import com.fogstream.testtask.service.ImageService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NewsQueryActionResolver implements GraphQLQueryResolver
{
	private NewsRepository newsRepository;
	private ImageService imageService;

	public NewsQueryActionResolver(NewsRepository newsRepository, ImageService imageService)
	{
		this.newsRepository = newsRepository;
		this.imageService = imageService;
	}

	@Transactional
	public NewsOutputDto getNews(Long id) throws Exception
	{
		News reqestedNews = newsRepository.findById(id).get();
		NewsOutputDto out = new NewsOutputDto(reqestedNews);
		if (reqestedNews.getPreview() != null)
			out.setImage(imageService.getImageData(reqestedNews.getPreview().getId()));
		return out;
	}

}
