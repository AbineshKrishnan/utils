package com.base.services.utils;

import org.springframework.context.MessageSource;
import reactor.core.publisher.Mono;

import java.util.Locale;

public class LocalizationMessageUtil {

    private LocalizationMessageUtil() {
    }

    public static LocalizationMessageUtil getInstance() {
        return new LocalizationMessageUtil();
    }

    public Mono<String> getMessageInfo(Locale locale, String messageKey, MessageSource messageSource) {
        return Mono.just(messageSource.getMessage(messageKey, null, locale));
    }

}
