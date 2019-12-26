package com.fogstream.testtask.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "images")
public class Image
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "images_sequence")
	@SequenceGenerator(name = "images_sequence", sequenceName = "images_sequence")
	private Long id;

	@Column
	private String extension;

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "profileImage")
	private List<User> profiles = new ArrayList<>();

	@JsonIgnore
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "preview")
	private List<News> news = new ArrayList<>();
}
