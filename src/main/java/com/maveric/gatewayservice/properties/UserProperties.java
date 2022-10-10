package com.maveric.gatewayservice.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("user-service")
public class UserProperties {

    private String path;

    private String uri;
}
