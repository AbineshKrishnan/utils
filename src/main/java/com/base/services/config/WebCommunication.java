package com.base.services.config;


import com.base.services.constants.MessageKeyConstants;
import com.base.services.dto.response.ApiTenantResponse;
import com.base.services.exception.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class WebCommunication {

    private final WebClient webClient;

    public WebCommunication(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<ApiTenantResponse> getTenant(String tenantId) {
        String url = "http://localhost:8080/apis/tenant?tenantId=" + tenantId;
        return webClient.get().uri(url).retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse -> clientResponse.bodyToMono(ApiTenantResponse.class)
                        .flatMap(object -> Mono.error(new BadRequestException(MessageKeyConstants.BAD_REQUEST))))
                .bodyToMono(ApiTenantResponse.class);
    }

}

