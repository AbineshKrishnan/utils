package com.base.services.utils;


import com.base.services.constants.MessageKeyConstants;
import com.base.services.dto.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Locale;

/**
 * This is a utility file for all common operations
 */
@Component
@Slf4j
public class CommonUtils {

    private CommonUtils() {
    }

    public static Mono<String> getHeaderValue(String headerKey) {
        return Mono.deferContextual(context -> {
            HttpHeaders httpHeaders = context.get(HttpHeaders.class);
            String headerValue = httpHeaders.getFirst(headerKey);
            return headerValue != null ? Mono.just(headerValue) : Mono.just("");
        });
    }

    private static <T extends ApiResponse> Mono<T> buildResponse(String messageKey, String responseCodeKey,
                                                                 MessageSource messageSource, T response) {
        return Mono.just(response)
                .flatMap(apiResponse -> getMessageInfo(messageKey, messageSource).doOnNext(apiResponse::setMessage)
                        .thenReturn(apiResponse))
                .flatMap(apiResponse -> getMessageInfo(responseCodeKey, messageSource).doOnNext(apiResponse::setStatusCode)
                        .thenReturn(apiResponse));
    }

    public static Mono<String> getMessageInfo(String messageKey, MessageSource messageSource) {
        return getHeaderValue(HttpHeaders.ACCEPT_LANGUAGE)
                .flatMap(locale -> LocalizationMessageUtil.getInstance()
                        .getMessageInfo(Locale.forLanguageTag(locale), messageKey, messageSource));
    }


    public static <T extends ApiResponse> Mono<T> successMessage(String messageKey, String responseCodeKey, MessageSource messageSource, T response) {
        return buildResponse(messageKey, responseCodeKey, messageSource, response);
    }

    public static <T extends ApiResponse> Mono<T> failureBadRequestMessage(String messageKey, MessageSource messageSource,
                                                                           T response) {
        return buildResponse(messageKey, MessageKeyConstants.ERROR_CODE_400, messageSource, response);
    }

    public static <T extends ApiResponse> Mono<T> failureResourceNotFoundMessage(String messageKey,
                                                                                 MessageSource messageSource, T response) {
        return buildResponse(messageKey, MessageKeyConstants.ERROR_CODE_404, messageSource, response);
    }

    public static <T extends ApiResponse> Mono<T> failureInvalidUserMessage(MessageSource messageSource, T response) {
        return buildResponse(MessageKeyConstants.INVALID_TOKEN, MessageKeyConstants.ERROR_CODE_403,
                messageSource, response);
    }

    public static <T extends ApiResponse> Mono<T> failureConflictMessage(String messageKey, MessageSource messageSource,
                                                                         T response) {
        return buildResponse(messageKey, MessageKeyConstants.ERROR_CODE_400, messageSource, response);
    }

    public static <T extends ApiResponse> Mono<T> failureInternalServerErrorMessage(String messageKey,
                                                                                    MessageSource messageSource, T response) {
        return buildResponse(messageKey, MessageKeyConstants.ERROR_CODE_500, messageSource, response);
    }

    public static <T extends ApiResponse> Mono<T> failureCustomExceptionMessage(String message, T response) {
        return Mono.just(response)
                .doOnNext(r -> r.setMessage(message))
                .doOnNext(r -> r.setStatusCode(String.valueOf(HttpStatus.BAD_REQUEST.value())));
    }

}
