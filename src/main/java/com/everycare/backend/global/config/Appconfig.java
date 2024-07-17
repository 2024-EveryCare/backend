package com.everycare.backend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

// RestTemplate 빈 설정 -> autowired로 사용할 수 있게끔
@Configuration
public class Appconfig {
    @Bean
public RestTemplate restTemplate(){
RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
restTemplate.getMessageConverters().add(0,new StringHttpMessageConverter(StandardCharsets.UTF_8));
    return new RestTemplate();
    }
}
