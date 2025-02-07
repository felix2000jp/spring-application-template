package dev.felix2000jp.springapplicationtemplate.auth.infrastructure.queue;

import dev.felix2000jp.springapplicationtemplate.auth.application.AppuserPublisher;
import dev.felix2000jp.springapplicationtemplate.auth.application.events.AppuserDeletedEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.modulith.test.ApplicationModuleTest;
import org.springframework.modulith.test.Scenario;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationModuleTest
@Testcontainers
class DefaultAppuserPublisherIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres");

    @Autowired
    private AppuserPublisher eventPublisher;

    @Test
    void publish_given_appuser_deleted_event_then_publish(Scenario scenario) {
        var appuserId = UUID.randomUUID();
        var appuserDeletedEvent = new AppuserDeletedEvent(appuserId);

        scenario
                .stimulate(() -> eventPublisher.publish(appuserDeletedEvent))
                .andWaitForEventOfType(AppuserDeletedEvent.class)
                .toArriveAndVerify(event -> assertThat(event.appuserId()).isEqualTo(appuserId));
    }

}