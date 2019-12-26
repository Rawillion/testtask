package com.fogstream.testtask.controller;

import com.fogstream.testtask.model.User;
import com.fogstream.testtask.repository.UserRepository;
import com.fogstream.testtask.service.CaptchaService;
import com.fogstream.testtask.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpRequest;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.Calendar;

@Controller
public class UserController
{
	private CaptchaService captchaService;
	private CustomUserDetailsService userDetailsService;

	public UserController(CaptchaService captchaService,
						  CustomUserDetailsService userDetailsService)
	{
		this.captchaService = captchaService;
		this.userDetailsService = userDetailsService;
	}

	@Value("${captcha.site-key}")
	private String siteCode;

	@RequestMapping(value = {"/editprofile"}, method = RequestMethod.GET)
	public String getProfilePage(Model model)
	{
		model.addAttribute("siteCode", siteCode);
		return "editprofile";
	}

	@RequestMapping(method = RequestMethod.POST, value = "/user")
	@ResponseBody
	public void create(@RequestBody User newUser, HttpServletRequest request) throws Exception
	{
		String captchaToken = request.getHeader("captchaToken");
		captchaService.captchaCheck(captchaToken, request);
		userDetailsService.createUser(newUser);
	}

}
