package com.fogstream.testtask.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.Calendar;

@Data
@NoArgsConstructor
@Entity
@Table(name = "news")
public class News
{
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "users_sequence")
	@SequenceGenerator(name = "users_sequence", sequenceName = "users_sequence")
	private Long id;

	@Column
	private String title;

	@Column
	private String text;

	@ManyToOne
	@JoinColumn(name = "user_created_id")
	private User createdBy;

	@Column(name = "data_created", columnDefinition = "timestamp with time zone")
	@JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
	@JsonProperty(value = "date_created")
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar dateCreated;

	@ManyToOne
	@JoinColumn(name = "category_id")
	private NewsCategory newsCategory;

	@ManyToOne
	@JoinColumn(name = "preview_image_id")
	private Image preview;
}
