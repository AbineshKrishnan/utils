package com.base.services.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class RequestNotAllowedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RequestNotAllowedException(String message) {
        super(message);
    }

	public RequestNotAllowedException(String message, Throwable cause) {
        super(message, cause);
    }
}
