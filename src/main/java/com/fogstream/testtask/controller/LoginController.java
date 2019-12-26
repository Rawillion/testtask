package com.fogstream.testtask.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController
{
	@Value("${captcha.site-key}")
	private String siteCode;

	@RequestMapping(value = {"/login", "/"}, method = RequestMethod.GET)
	public String getLoginPage(Model model)
	{
		model.addAttribute("siteCode", siteCode);
		return "login";
	}
}
