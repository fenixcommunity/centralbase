package com.fenixcommunity.centralspace.app.configuration.swaggerdoc;

import static com.fenixcommunity.centralspace.utilities.common.DevTool.getSimpleClassName;
import static com.fenixcommunity.centralspace.utilities.common.Var.DOMAIN_URL;
import static com.fenixcommunity.centralspace.utilities.common.Var.EMAIL;
import static com.google.common.collect.Lists.newArrayList;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.util.List;

import com.fenixcommunity.centralspace.app.rest.exception.ErrorDetails;
import com.google.common.base.Predicate;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.ResponseMessageBuilder;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.ResponseMessage;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@EnableSwagger2 //@EnableSwagger2WebMvc 3.0.0v
@Configuration
//@Import({BeanValidatorPluginsConfiguration.class}) SpringDataRestConfiguration.class no works for latest Spring
//@Profile(Profiles.SWAGGER_ENABLED_PROFILE)
@FieldDefaults(level = PRIVATE)
public class SwaggerConfig implements WebMvcConfigurer {

    private static final String REST_PACKAGE = "com.fenixcommunity.centralspace.app.rest";

    @Value("${api.path}")
    private String API_PATH;

    @Value("${springfox.swagger2.host}")
    private String swagger2Host;

    @Bean
    public Docket getDocumentation() {
        return new Docket(DocumentationType.SWAGGER_2)
//                .host(swagger2Host)
                .useDefaultResponseMessages(false)
                .globalResponseMessage(RequestMethod.GET, errorList())
                .globalResponseMessage(RequestMethod.POST, errorList())
                .globalResponseMessage(RequestMethod.PUT, errorList())
                .globalResponseMessage(RequestMethod.DELETE, errorList())
                .select()
                .apis(RequestHandlerSelectors.basePackage(REST_PACKAGE))
                .paths(pathsFilter())
                .build()
                .apiInfo(metadata());
    }

//    3.0.0v
//    @Bean
//    public EmailAnnotationPlugin emailPlugin() {
//        return new EmailAnnotationPlugin();
//    }

    private Predicate<String> pathsFilter() {
        return input ->
                input.matches(API_PATH + "/account/.*") ||
                        input.matches(API_PATH + "/account-flux/.*") ||
                        input.matches(API_PATH + "/doc/.*") ||
                        input.matches(API_PATH + "/mail/.*") ||
                        input.matches(API_PATH + "/password/.*") ||
                        input.matches(API_PATH + "/register/.*") ||
                        input.matches(API_PATH + "/logger/.*") ||
                        input.matches(API_PATH + "/cross/.*") ||
                        input.matches(API_PATH + "/resource/.*");
    }

    private ApiInfo metadata() {
        return new ApiInfoBuilder()
                .title("Fenix community")
                .description("Centralspace app")
                .version("Version 1.0")
                .contact(new Contact("MK", DOMAIN_URL, EMAIL))
                .build();
    }

    private List<ResponseMessage> errorList() {
        return newArrayList(
                new ResponseMessageBuilder().code(NOT_FOUND.value())
                        .responseModel(new ModelRef(getSimpleClassName(ErrorDetails.class)))
                        .message(ErrorDetails.toStringModel())
                        .build(),
                new ResponseMessageBuilder().code(INTERNAL_SERVER_ERROR.value())
                        .responseModel(new ModelRef(getSimpleClassName(ErrorDetails.class)))
                        .message(ErrorDetails.toStringModel())
                        .build()
                //             .responseModel(new ModelRef(getClassName(ErrorDetails.class))).build());
        );
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry
                .addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

}