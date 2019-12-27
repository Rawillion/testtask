package com.fogstream.testtask.model.Dto;

import com.fogstream.testtask.model.News;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class NewsInputDto extends NewsDto
{
	public NewsInputDto(News news)
	{
		super(news);
	}
	private Long id;
	private String imageName;
}
