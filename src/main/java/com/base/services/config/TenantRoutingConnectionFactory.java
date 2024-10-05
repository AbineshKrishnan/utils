package com.base.services.config;

import com.base.services.dto.response.TenantResponse;
import com.base.services.utils.CommonUtils;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.connection.lookup.AbstractRoutingConnectionFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Component
public class TenantRoutingConnectionFactory extends AbstractRoutingConnectionFactory {

    private final WebCommunication webCommunication;
    private final String r2dbcUrl;

    private static final String TENANT_ID = "X-Tenant-ID";

    public TenantRoutingConnectionFactory(WebCommunication webCommunication, @Value("${spring.r2dbc.url}") String r2dbcUrl) {
        this.webCommunication = webCommunication;
        this.r2dbcUrl = r2dbcUrl;
        // Initialize with the default connection factory
        ConnectionFactory defaultFactory = createDefaultConnectionFactory();
        Map<String, ConnectionFactory> tenantConnectionFactories = Map.of("default", defaultFactory);
        // Set the target connection factories
        setTargetConnectionFactories(tenantConnectionFactories);
        // Set the default connection factory
        setDefaultTargetConnectionFactory(defaultFactory);
    }

    private ConnectionFactory createDefaultConnectionFactory() {
        return ConnectionFactories.get(r2dbcUrl);
    }

    @Override
    @NonNull
    protected Mono<ConnectionFactory> determineTargetConnectionFactory() {
        return CommonUtils.getHeaderValue(TENANT_ID)
                .flatMap(tenantId -> tenantId.isBlank()
                        ? Mono.just(createDefaultConnectionFactory())
                        : webCommunication.getTenant(tenantId)
                        .map(apiTenantResponse -> this.createTenantConnectionFactory(apiTenantResponse.getResult())));
    }

    @Override
    @NonNull
    protected Mono<Object> determineCurrentLookupKey() {
        return Mono.empty();
    }

    public ConnectionFactory createTenantConnectionFactory(TenantResponse tenant) {
        return ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(DRIVER, tenant.getDriver())
                .option(HOST, tenant.getDbUrl())
                .option(PORT, tenant.getPort())
                .option(USER, tenant.getDbUsername())
                .option(PASSWORD, tenant.getDbPassword())
                .option(DATABASE, tenant.getDbName())
                .build());
    }

}