package com.base.services.config;

import com.base.services.dto.response.TenantResponse;
import com.base.services.utils.CommonUtils;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import lombok.NonNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.connection.lookup.AbstractRoutingConnectionFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Map;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Configuration
@Component
public class TenantRoutingConnectionFactory extends AbstractRoutingConnectionFactory {

    private final WebCommunication webCommunication;

    private static final String TENANT_ID = "X-Tenant-ID";
    private static final String DB_DATA = "postgres";

    public TenantRoutingConnectionFactory(WebCommunication webCommunication) {
        this.webCommunication = webCommunication;
        // Initialize with the default connection factory
        ConnectionFactory defaultFactory = createDefaultConnectionFactory();
        Map<String, ConnectionFactory> tenantConnectionFactories = Map.of("default", defaultFactory);
        // Set the target connection factories
        setTargetConnectionFactories(tenantConnectionFactories);
        // Set the default connection factory
        setDefaultTargetConnectionFactory(defaultFactory);
    }

    private ConnectionFactory createDefaultConnectionFactory() {
        return ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(DRIVER, "postgresql")
                .option(HOST, "localhost")
                .option(PORT, 5432)
                .option(USER, DB_DATA)
                .option(PASSWORD, DB_DATA)
                .option(DATABASE, "tenant123")
                .build());
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