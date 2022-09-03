package com.maveric.gatewayservice.config;

import com.maveric.gatewayservice.properties.BalanceProperties;
import com.maveric.gatewayservice.properties.UserProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayServiceConfig {

   @Autowired
   UserProperties userProperties;

   @Autowired
   BalanceProperties balanceProperties;

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(routeToUser -> routeToUser.path(userProperties.getPath())
                        .uri(userProperties.getUri()))

                .route(routeToBalance -> routeToBalance.path(balanceProperties.getPath())
                        .uri(balanceProperties.getUri()))
                .build();
    }
}
