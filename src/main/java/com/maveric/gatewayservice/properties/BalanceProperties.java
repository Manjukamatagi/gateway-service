package com.maveric.gatewayservice.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("balance-service")
public class BalanceProperties {
    private String path;

    private String uri;
}
