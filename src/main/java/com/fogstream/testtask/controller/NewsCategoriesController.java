package com.fogstream.testtask.controller;

import com.fogstream.testtask.model.NewsCategory;
import com.fogstream.testtask.model.User;
import com.fogstream.testtask.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;
import java.util.List;

@Controller
public class NewsCategoriesController
{
	private UserRepository userRepository;

	public NewsCategoriesController(UserRepository userRepository)
	{
		this.userRepository = userRepository;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/currentCategories")
	@ResponseBody
	public List<NewsCategory> getCurrentCategories(Principal principal)
	{
		String userRequested = principal.getName();
		User currentUser = userRepository.findByLogin(userRequested);
		return currentUser.getNewsCategories();
	}

}
