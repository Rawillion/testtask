package com.fogstream.testtask.model.Dto;

import com.fogstream.testtask.model.User;

public class UserOutputInfoDto extends UserInfoDto
{
	public UserOutputInfoDto(User user)
	{
		super(user);
		this.registrationDate = user.getRegistrationDate().getTimeInMillis();
	}

	private long registrationDate;
}
