package com.maveric.gatewayservice.config;

//import com.maveric.gatewayservice.filter.AuthenticationPreFilter;
import com.maveric.gatewayservice.filter.AuthenticationPreFilter;
import com.maveric.gatewayservice.properties.AccountProperties;
import com.maveric.gatewayservice.properties.BalanceProperties;
import com.maveric.gatewayservice.properties.UserProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Configuration
public class GatewayServiceConfig {

   @Autowired
   UserProperties userProperties;

   @Autowired
   BalanceProperties balanceProperties;

   @Autowired
   AccountProperties accountProperties;

   @Autowired
   AuthenticationPreFilter authenticationPreFilter;

    private final WebClient.Builder webClientBuilder;

    public GatewayServiceConfig(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
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
                .build();
    }

}
