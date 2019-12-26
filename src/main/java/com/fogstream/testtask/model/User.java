package com.fogstream.testtask.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_sequence")
	@SequenceGenerator(name = "users_sequence", sequenceName = "users_sequence")
	private Long id;

	@Column
	private String login;

	@Column
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;

	@Column(name = "first_name")
	@JsonProperty(value = "firstname")
	private String firstName;

	@Column(name = "last_name")
	@JsonProperty(value = "lastname")
	private String lastName;

	@Column
	private String email;

	@Column(name = "birthday", columnDefinition = "timestamp with time zone")
	@JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
	@JsonProperty(value = "birthday")
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar birthDay;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
			name = "users_news_categories",
			joinColumns = @JoinColumn(name = "user_id"),
			inverseJoinColumns = @JoinColumn(name = "category_id"))
	private List<NewsCategory> newsCategories = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "profile_image_id")
	private Image profileImage;

	@Column
	private String gender;

	@Column(name = "registration_date", columnDefinition = "timestamp with time zone")
	@JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
	@JsonProperty(value = "registration_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar registrationDate;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities()
	{
		return null;
	}

	@Override
	public String getUsername()
	{
		return getLogin();
	}

	@Override
	public boolean isAccountNonExpired()
	{
		return true;
	}

	@Override
	public boolean isAccountNonLocked()
	{
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired()
	{
		return true;
	}

	@Override
	public boolean isEnabled()
	{
		return true;
	}

}
