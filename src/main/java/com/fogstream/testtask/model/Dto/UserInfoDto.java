package com.fogstream.testtask.model.Dto;

import com.fogstream.testtask.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
public abstract class UserInfoDto
{
	public UserInfoDto(User user)
	{
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.email = user.getEmail();
		this.birthday = Optional.ofNullable(user.getBirthDay()).map(Calendar::getTimeInMillis).orElse(-1L);
		this.gender = user.getGender();
	}

	private String firstName;
	private String lastName;
	private String email;
	private long birthday;
	private List<Long> newsCategories;
	private String image;
	private String gender;
}
