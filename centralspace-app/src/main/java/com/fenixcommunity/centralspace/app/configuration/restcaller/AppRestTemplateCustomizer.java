package com.fenixcommunity.centralspace.app.configuration.restcaller;

import lombok.AllArgsConstructor;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

@AllArgsConstructor
public class AppRestTemplateCustomizer implements RestTemplateCustomizer {
    private final String username;
    private final String password;

    @Override
    public void customize(RestTemplate restTemplate) {
        restTemplate.getInterceptors().add(new AppClientHttpRequestInterceptor());
        restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(username, password));
    }
}