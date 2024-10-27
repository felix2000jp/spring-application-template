package dev.felix2000jp.springapplicationtemplate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.core.ApplicationModules;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class SpringApplicationTemplateApplicationTests {

    @Test
    void contextLoads() {
        ApplicationModules.of(SpringApplicationTemplateApplication.class).verify();
    }

}
