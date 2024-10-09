package com.base.services.config;

import com.base.services.dto.response.TenantResponse;
import com.base.services.utils.CommonUtils;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import jakarta.annotation.PreDestroy;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.r2dbc.connection.lookup.AbstractRoutingConnectionFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import java.util.Map;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;

@Component
public class TenantRoutingConnectionFactory extends AbstractRoutingConnectionFactory {

    private final WebCommunication webCommunication;
    private final String r2dbcUrl;
    Cache<String, ConnectionFactory> tenantConnectionFactoryCache = Caffeine.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

    private static final String TENANT_ID = "X-Tenant-ID";

    public TenantRoutingConnectionFactory(WebCommunication webCommunication, @Value("${spring.r2dbc.url}") String r2dbcUrl) {
        this.webCommunication = webCommunication;
        this.r2dbcUrl = r2dbcUrl;
        ConnectionFactory defaultFactory = createDefaultConnectionFactory();
        Map<String, ConnectionFactory> tenantConnectionFactories = Map.of("default", defaultFactory);
        setTargetConnectionFactories(tenantConnectionFactories);
        setDefaultTargetConnectionFactory(defaultFactory);
    }

    private ConnectionFactory createDefaultConnectionFactory() {
        return ConnectionFactories.get(r2dbcUrl);
    }

    @Override
    @NonNull
    protected Mono<ConnectionFactory> determineTargetConnectionFactory() {
        return CommonUtils.getHeaderValue(TENANT_ID)
                .flatMap(tenantId -> {
                    if (tenantId.isBlank()) {
                        return Mono.just(createDefaultConnectionFactory());
                    }

                    // Check cache first
                    ConnectionFactory cachedFactory = tenantConnectionFactoryCache.getIfPresent(tenantId);
                    if (cachedFactory != null) {
                        return Mono.just(cachedFactory);
                    }

                    // Retrieve and create a new connection factory
                    return webCommunication.getTenant(tenantId)
                            .map(apiTenantResponse -> {
                                ConnectionFactory factory = createTenantConnectionFactory(apiTenantResponse.getResult());
                                tenantConnectionFactoryCache.put(tenantId, factory);  // Cache the connection factory
                                return factory;
                            });
                });
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

    @PreDestroy
    public void clearCacheOnShutdown() {
        tenantConnectionFactoryCache.invalidateAll();
    }

}