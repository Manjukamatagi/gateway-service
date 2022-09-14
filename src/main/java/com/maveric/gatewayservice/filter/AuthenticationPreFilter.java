package com.maveric.gatewayservice.filter;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maveric.gatewayservice.constant.ErrorMessageConstant;
import com.maveric.gatewayservice.model.ExceptionResponseModel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.gateway.filter.GatewayFilter;

import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;
import java.util.List;
import java.util.function.Predicate;

@Component
@Slf4j
public class AuthenticationPreFilter extends AbstractGatewayFilterFactory<AuthenticationPreFilter.Config> {

    private final WebClient.Builder webClientBuilder;

    public AuthenticationPreFilter(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            String bearerToken = request.getHeaders().getFirst("Authorization");

                return webClientBuilder.build().get()
                        .uri("http://localhost:3000/api/v1/auth/validateToken")
                        .header("Authorization", bearerToken)
                        .retrieve().bodyToMono(ConnValidationResponse.class)
                        .map(response -> {
                            exchange.getRequest().mutate().header("userId", response.getUserId());

                            return exchange;
                        }).flatMap(chain::filter).onErrorResume(error -> {
                            log.info("Error Happened");
                            HttpStatus errorStatus=HttpStatus.FORBIDDEN;
                            String errorCode = "";
                            String errorMsg = "";
                            if (error instanceof WebClientResponseException) {
                                WebClientResponseException webCLientException = (WebClientResponseException) error;
                                errorCode = ErrorMessageConstant.JWT_ERROR_CODE;
                                errorMsg = ErrorMessageConstant.JWT_ERROR_MESSAGE;

                            } else {
                                errorCode = ErrorMessageConstant.JWT_ERROR_CODE;
                                errorMsg = ErrorMessageConstant.JWT_ERROR_MESSAGE;
                            }
//                            AuthorizationFilter.AUTH_FAILED_CODE
                            return onError(exchange, errorCode ,errorMsg, errorStatus);
                        });
        };


    }


    private Mono<Void> onError(ServerWebExchange exchange, String errCode, String err, HttpStatus httpStatus) {
        DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        try {
            response.getHeaders().add("Content-Type", "application/json");
            ExceptionResponseModel data = new ExceptionResponseModel(errCode, err);
            byte[] byteData = objectMapper.writeValueAsBytes(data);
            return response.writeWith(Mono.just(byteData).map(t -> dataBufferFactory.wrap(t)));

        } catch (JsonProcessingException e) {
            e.printStackTrace();

        }
        return response.setComplete();
    }

    @Getter
    @Builder
    @ToString
    public static class ConnValidationResponse {
        private String status;
        private boolean isAuthenticated;
        private String methodType;
        private String userId;
    }


    @NoArgsConstructor
    public static class Config {


    }
}
