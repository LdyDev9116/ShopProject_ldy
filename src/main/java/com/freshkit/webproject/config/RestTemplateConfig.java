package com.freshkit.webproject.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig { //결제 관련

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
