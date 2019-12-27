package com.fogstream.testtask.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
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

	@Transactional
	public String updateUser(UserInputInfoDto updatingInfo, DataFetchingEnvironment env) throws Exception
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
				if (imageForDelete != null && imageForDelete.getProfiles().size() == 0 && imageForDelete.getNews().size() == 1)
				{
					currentUser.setProfileImage(null);
					userRepository.save(currentUser);
					imageService.deleteImage(imageForDelete);
				}
				currentUser.setProfileImage(imageService.createImageFrom64BaseString(updatingInfo.getImage(), updatingInfo.getImageName()));
			}

			currentUser.setGender(updatingInfo.getGender());
			userRepository.save(currentUser);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return getReturnMessage(ex.getMessage());
		}
		return getReturnMessage("Success");
	}

	private String getReturnMessage(String message) throws Exception
	{
		HashMap<String, String> map = new HashMap<>();
		map.put("message", message);
		return new ObjectMapper().writeValueAsString(map);
	}
}
