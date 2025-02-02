package dev.felix2000jp.springapplicationtemplate;

import org.springframework.boot.SpringApplication;
import org.springframework.modulith.Modulith;

@Modulith(sharedModules = "shared")
class SpringApplicationTemplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringApplicationTemplateApplication.class, args);
    }

}
