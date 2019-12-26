package com.fogstream.testtask.model.Dto;

import com.fogstream.testtask.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class UserInputInfoDto extends UserInfoDto
{
	public UserInputInfoDto(User user)
	{
		super(user);
	}
	private String imageName;
}
