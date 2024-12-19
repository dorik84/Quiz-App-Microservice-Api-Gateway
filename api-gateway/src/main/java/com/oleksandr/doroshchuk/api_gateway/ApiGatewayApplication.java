package com.oleksandr.doroshchuk.api_gateway;

import java.time.LocalDateTime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.route.RouteLocator; 
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;

@SpringBootApplication
@EnableDiscoveryClient 
public class ApiGatewayApplication {

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("quiz-service", r -> r
                .path("/api-quiz/**")
                .filters(f -> f
                    .rewritePath("/api-quiz/(?<segment>.*)", "/${segment}")
                    .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                .uri("lb://QUIZ-SERVICE"))
            
            .route("question-service", r -> r
                .path("/api-question/**")
                .filters(f -> f
                    .rewritePath("/api-question/(?<segment>.*)", "/${segment}")
                    .circuitBreaker(config -> config
                        .setName("questionServiceBreaker")
                        .setFallbackUri("forward:/question-fallback")))
                .uri("lb://QUESTION-SERVICE"))
            
            .build();
    }
}



	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

}
