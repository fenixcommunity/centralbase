package com.fenixcommunity.centralspace;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class CentralspaceApplication extends SpringBootServletInitializer {

    //jest uzywamy jsp, jak nie to to usuwamy także extends
    // zaciagamy sources
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(CentralspaceApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(CentralspaceApplication.class, args);
    }

}

