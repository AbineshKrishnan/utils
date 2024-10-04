package com.base.services.exception;


import com.base.services.constants.MessageKeyConstants;
import com.base.services.dto.response.ApiResponse;
import com.base.services.utils.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import pirai.tools.validations.ValidateUtil;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@ControllerAdvice
public class CustomExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ApiResponse>> handleValidationExceptions(WebExchangeBindException ex) {
        return Mono.just(ex)
                .flatMap(this::getResponse)
                .flatMap(apiResponse -> {
                    List<String> errors = ex.getBindingResult().getAllErrors().stream().map(ObjectError::getDefaultMessage).toList();
                    String errorMessage = String.join(", ", errors);
                    log.error("handleValidationExceptions :: {}", errorMessage);
                    return CommonUtils.failureCustomExceptionMessage(errorMessage, apiResponse);
                })
                .flatMap(apiResponse -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse)));
    }

    @ExceptionHandler(InvalidUserException.class)
    public Mono<ResponseEntity<ApiResponse>> handleInvalidUserExceptions(InvalidUserException ex) {
        return Mono.just(ex)
                .flatMap(this::getResponse)
                .flatMap(apiResponse -> CommonUtils.failureInvalidUserMessage(messageSource, apiResponse))
                .flatMap(apiResponse -> Mono.just(ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse)));
    }

    @ExceptionHandler(CustomException.class)
    public Mono<ResponseEntity<ApiResponse>> handleCustomExceptions(CustomException ex) {
        return normalMessageException(ex, ex.getMessage());
    }

    private Mono<ResponseEntity<ApiResponse>> normalMessageException(Exception ex, String message) {
        return Mono.just(ex)
                .flatMap(this::getResponse)
                .flatMap(apiResponse -> CommonUtils.failureCustomExceptionMessage(message, apiResponse))
                .flatMap(apiResponse -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse)));
    }

    @ExceptionHandler(BadRequestException.class)
    public Mono<ResponseEntity<ApiResponse>> handleBadRequestExceptions(BadRequestException ex) {
        return Mono.just(ex)
                .flatMap(this::getResponse)
                .flatMap(apiResponse -> CommonUtils.failureBadRequestMessage(ex.getMessage(), messageSource, apiResponse))
                .flatMap(apiResponse -> Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse)));
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public Mono<ResponseEntity<ApiResponse>> handleResourceNotFoundExceptions(ResourceNotFoundException ex) {
        return Mono.just(ex)
                .flatMap(this::getResponse)
                .flatMap(apiResponse -> CommonUtils.failureResourceNotFoundMessage(ex.getMessage(), messageSource, apiResponse))
                .flatMap(apiResponse -> Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse)));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiResponse>> handleExceptions(Exception ex) {
        return Mono.just(ex)
                .flatMap(this::getResponse)
                .flatMap(apiResponse -> CommonUtils.failureInternalServerErrorMessage(MessageKeyConstants.INTERNAL_SERVER_ERROR, messageSource, apiResponse))
                .flatMap(apiResponse -> Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse)));
    }

    private Mono<ApiResponse> getResponse(Exception ex) {
        return Mono.just(ex.getStackTrace())
                .map(stackTraceElements -> Arrays.stream(stackTraceElements)
                        .filter(s -> s.getClassName().toUpperCase().contains("service.impl".toUpperCase()))
                        .findFirst().orElse(new StackTraceElement("", "", "", 0)))
                .doOnNext(st -> {
                    if (!ValidateUtil.isNullEmpty(ex.getCause())) log.error(ex.getCause().getMessage());
                    log.error("{} :: {} - {} - line Number {} ", ex.getMessage(), st.getFileName(), st.getMethodName(), st.getLineNumber());
                }).flatMap(st -> Mono.just(new ApiResponse()).doOnNext(apiResponse -> apiResponse.setStatus(Boolean.FALSE)));
    }

}
