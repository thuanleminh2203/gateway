package com.venesa.gateway.component;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class WrapperResponseData {
	
	public <T> ResponseEntity<T> success(T body){
		return new ResponseEntity<>(body, HttpStatus.OK);
	}
	
	public <T> ResponseEntity<T> error(T body, HttpStatus status){
		return new ResponseEntity<>(body, status);
	}

}
