package com.fogstream.testtask.model.Dto;

import com.fogstream.testtask.model.News;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class NewsOutputDto extends NewsDto
{
	public NewsOutputDto(News news)
	{
		super(news);
		this.creationDate = news.getDateCreated().getTimeInMillis();
	}
	private Long creationDate;
}
