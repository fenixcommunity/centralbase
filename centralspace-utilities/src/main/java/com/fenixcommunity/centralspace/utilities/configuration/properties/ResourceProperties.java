package com.fenixcommunity.centralspace.utilities.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotBlank;

@Component
@ConfigurationProperties(prefix = "resource")
@PropertySource("classpath:resource.properties")
@Getter
@Setter
public class ResourceProperties {

    @NotBlank
    private String imageUrl;
}