package com.fenixcommunity.centralspace.utilities.configuration;

import com.fenixcommunity.centralspace.utilities.configuration.properties.ResourceProperties;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import static lombok.AccessLevel.PRIVATE;

@Configuration
@ComponentScan({"com.fenixcommunity.centralspace.utilities"})
@EnableConfigurationProperties(ResourceProperties.class)
@FieldDefaults(level = PRIVATE, makeFinal = true)
public class UtilitiesConfig {
}
