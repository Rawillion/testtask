package com.fogstream.testtask.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "news_category")
public class NewsCategory
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "news_category_sequence")
	@SequenceGenerator(name = "news_category_sequence", sequenceName = "news_category_sequence")
	private Long id;

	@Column
	private String name;

	@JsonIgnore
	@ManyToMany(mappedBy = "newsCategories")
	List<User> users = new ArrayList<>();

	@JsonIgnore
	@OneToMany(mappedBy = "newsCategory")
	List<News> news = new ArrayList<>();
}
