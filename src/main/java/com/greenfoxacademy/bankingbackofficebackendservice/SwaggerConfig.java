package com.greenfoxacademy.bankingbackofficebackendservice;

import java.util.Collections;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SwaggerConfig {

  @Bean
  public Docket productApi() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.greenfoxacademy.bankingbackofficebackendservice"))
        .build()
        .apiInfo(apiInfo());
  }

  private ApiInfo apiInfo() {
    return new ApiInfo(
        "Banking back office service",
        "This application services an imaginary bank back-office system. The client applications can use the system APIs, to interact with the application.",
        "1.0",
        "",
        new Contact("", "", ""),
        "Free to use", "",
        Collections.emptyList());
  }

}
