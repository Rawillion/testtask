package com.fogstream.testtask.translator;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
public class Result {
	private int code;

	private String message;

	private Long identity;

	private String key;

	private String fullMessage;

	public void setParameters(int code, String message) {
		this.code = code;
		this.message = message;
		this.fullMessage = message;
	}

	public Result() {
		this.code = 0;
		this.message = null;
		this.fullMessage = null;
	}

	public Result(int code, String message) {
		this.code = code;
		this.message = message;
		this.fullMessage = message;
	}

	public Result(int code, String message, String fullMessage) {
		this.code = code;
		this.message = message;
		this.fullMessage = fullMessage;
	}
}
