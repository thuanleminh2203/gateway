package com.venesa.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
	private long timestamp = new Date().getTime();
	private String status;
	private String error;
	private String message;
	public ErrorResponse(String status, String error, String message) {
		this.status = status;
		this.error = error;
		this.message = message;
	}
	
	
//	private String path;
}
