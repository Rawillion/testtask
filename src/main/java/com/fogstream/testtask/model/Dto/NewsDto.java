package com.fogstream.testtask.model.Dto;

import com.fogstream.testtask.model.News;
import com.fogstream.testtask.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class NewsDto
{
	public NewsDto(News news)
	{
		this.title = news.getTitle();
		this.text = news.getText();
		this.newsCategory = news.getNewsCategory().getId();
	}

	private String title;
	private String text;
	private Long newsCategory;
	private String image;
}
