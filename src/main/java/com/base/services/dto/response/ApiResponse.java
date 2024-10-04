package com.base.services.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class ApiResponse implements Serializable {
	
	@Serial
	private static final long serialVersionUID = 1L;

	private Boolean status = true;
	
	private String message;
	
	private String statusCode;
		
}
