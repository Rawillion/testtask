package com.fogstream.testtask.resolver;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.fogstream.testtask.model.Dto.UserOutputInfoDto;
import com.fogstream.testtask.model.Image;
import com.fogstream.testtask.model.NewsCategory;
import com.fogstream.testtask.model.User;
import com.fogstream.testtask.repository.UserRepository;
import com.fogstream.testtask.service.ImageService;
import graphql.schema.DataFetchingEnvironment;
import graphql.servlet.context.DefaultGraphQLServletContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Component
public class UserQueryActionResolver implements GraphQLQueryResolver
{
	private UserRepository userRepository;
	private ImageService imageService;

	public UserQueryActionResolver(UserRepository userRepository, ImageService imageService)
	{
		this.userRepository = userRepository;
		this.imageService = imageService;
	}

	public UserOutputInfoDto getCurrentUser(DataFetchingEnvironment env) throws Exception
	{
		String userRequested = ((DefaultGraphQLServletContext)env.getContext()).getHttpServletRequest().getUserPrincipal().getName();
		User currentUser = userRepository.findByLogin(userRequested);
		UserOutputInfoDto out = new UserOutputInfoDto(currentUser);
		out.setImage(imageService.getImageData(Optional.ofNullable(currentUser.getProfileImage()).map(Image::getId).orElse(-1L)));
		out.setNewsCategories(currentUser.getNewsCategories().stream().map(NewsCategory::getId).collect(Collectors.toList()));
		return out;
	}

}
