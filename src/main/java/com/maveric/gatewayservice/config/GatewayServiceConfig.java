package com.maveric.gatewayservice.config;

import com.maveric.gatewayservice.filter.AuthenticationPreFilter;
import com.maveric.gatewayservice.properties.*;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class GatewayServiceConfig {



    public GatewayServiceConfig(WebClient.Builder webClientBuilder) {
        webClientBuilder = webClientBuilder;
    }

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder,AuthenticationPreFilter authenticationPreFilter,
                                      AuthProperties authProperties,BalanceProperties balanceProperties,TransactionProperties transactionProperties,
                                      AccountProperties accountProperties,UserProperties userProperties) {
        return builder.routes()
                .route(routeToUser -> routeToUser.path(userProperties.getPath())
                        .filters(f ->
                                f.filter(authenticationPreFilter.apply(
                                                new AuthenticationPreFilter.Config())))
                        .uri(userProperties.getUri()))

                .route(routeToBalance -> routeToBalance.path(balanceProperties.getPath())
                        .filters(f ->
                                f.filter(authenticationPreFilter.apply(
                                        new AuthenticationPreFilter.Config())))
                        .uri(balanceProperties.getUri()))
                .route(routeToAccount -> routeToAccount.path(accountProperties.getPath())
                        .filters(f ->
                                f.filter(authenticationPreFilter.apply(
                                        new AuthenticationPreFilter.Config())))
                        .uri(accountProperties.getUri()))
                .route(routeToAccount -> routeToAccount.path(transactionProperties.getPath())
                        .filters(f ->
                                f.filter(authenticationPreFilter.apply(
                                        new AuthenticationPreFilter.Config())))
                        .uri(transactionProperties.getUri()))
                .route(routeToAccount -> routeToAccount.path(authProperties.getPath())
                        .uri(authProperties.getUri()))
                .build();
    }

}
