package com.base.services.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ValidateEditException extends RuntimeException{

	private static final long serialVersionUID = 1L;

    public ValidateEditException(String message) {
        super(message);
    }

    public ValidateEditException(String message, Throwable cause) {
        super(message, cause);
    }
}
