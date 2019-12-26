package com.fogstream.testtask.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.fogstream.testtask.model.Dto.UserInputInfoDto;
import com.fogstream.testtask.model.Image;
import com.fogstream.testtask.model.NewsCategory;
import com.fogstream.testtask.model.User;
import com.fogstream.testtask.repository.NewsCategoryRepository;
import com.fogstream.testtask.repository.UserRepository;
import com.fogstream.testtask.service.ImageService;
import graphql.schema.DataFetchingEnvironment;
import graphql.servlet.context.DefaultGraphQLServletContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

@Component
public class UserMutationActionResolver implements GraphQLMutationResolver
{
	private UserRepository userRepository;
	private NewsCategoryRepository newsCategoryRepository;
	private ImageService imageService;

	public UserMutationActionResolver(UserRepository userRepository,
									  NewsCategoryRepository newsCategoryRepository,
									  ImageService imageService)
	{
		this.userRepository = userRepository;
		this.newsCategoryRepository = newsCategoryRepository;
		this.imageService = imageService;
	}

	public String updateUser(UserInputInfoDto updatingInfo, DataFetchingEnvironment env)
	{
		try
		{
			String userUpdating = ((DefaultGraphQLServletContext)env.getContext()).getHttpServletRequest().getUserPrincipal().getName();
			User currentUser = userRepository.findByLogin(userUpdating);
			currentUser.setFirstName(updatingInfo.getFirstName());
			currentUser.setLastName(updatingInfo.getLastName());
			Calendar birthday = Calendar.getInstance();
			birthday.setTimeInMillis(updatingInfo.getBirthday());
			currentUser.setBirthDay(birthday);
			currentUser.setEmail(updatingInfo.getEmail());
			List<NewsCategory> categories = new ArrayList<>();
			for (Long ncategoryId : updatingInfo.getNewsCategories())
				categories.add(newsCategoryRepository.getOne(ncategoryId));
			currentUser.setNewsCategories(categories);
			if (!updatingInfo.getImageName().equals(""))
			{
				Image imageForDelete = currentUser.getProfileImage();
				currentUser.setProfileImage(null);
				userRepository.save(currentUser);
				imageService.deleteImage(imageForDelete);
				currentUser.setProfileImage(imageService.createImageFrom64BaseString(updatingInfo.getImage(), updatingInfo.getImageName()));
			}
			currentUser.setGender(updatingInfo.getGender());
			userRepository.save(currentUser);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return ex.getMessage();
		}
		return "Success";
	}
}
