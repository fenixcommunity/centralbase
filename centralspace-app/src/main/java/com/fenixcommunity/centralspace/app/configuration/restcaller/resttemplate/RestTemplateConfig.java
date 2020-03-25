package com.fenixcommunity.centralspace.app.configuration.restcaller.resttemplate;

import static com.fenixcommunity.centralspace.app.configuration.security.autosecurity.SecurityRole.BASIC;
import static com.fenixcommunity.centralspace.utilities.common.Var.PASSWORD;
import static lombok.AccessLevel.PRIVATE;

import com.fenixcommunity.centralspace.app.configuration.restcaller.resttemplate.retrywrapper.RestTemplateRetryWrapper;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@Configuration
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class RestTemplateConfig {

    @Bean
    @DependsOn(value = {"appRestTemplateCustomizer"})
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder(appRestTemplateCustomizer());
    }

    //todo - to get resources inside app, in future please handle autorization for other external services
    @Bean
    public AppRestTemplateCustomizer appRestTemplateCustomizer() {
        return new AppRestTemplateCustomizer(BASIC.name(), PASSWORD);
    }

    @Bean
    @DependsOn(value = {"restTemplateBuilder"})
    public RestTemplateRetryWrapper restTemplateRetryWrapper() {
        return new RestTemplateRetryWrapper(restTemplateBuilder().build());
    }


}