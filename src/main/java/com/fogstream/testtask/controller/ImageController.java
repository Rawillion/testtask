package com.fogstream.testtask.controller;

import com.fogstream.testtask.service.ImageService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/image")
public class ImageController
{
	private ImageService imageService;

	public ImageController(ImageService imageService)
	{
		this.imageService = imageService;
	}

	@RequestMapping(path = "/view/{id}", method = RequestMethod.GET)
	@ResponseBody
	public String getView(@PathVariable long id) throws Exception
	{
		return imageService.getImageData(id);
	}
}
