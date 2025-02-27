package dev.felix2000jp.springapplicationtemplate.auth.infrastructure.queue;

import dev.felix2000jp.springapplicationtemplate.auth.application.events.AppuserDeletedEvent;
import dev.felix2000jp.springapplicationtemplate.auth.application.AppuserPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
class DefaultAppuserPublisher implements AppuserPublisher {

    private final ApplicationEventPublisher events;

    DefaultAppuserPublisher(ApplicationEventPublisher events) {
        this.events = events;
    }

    @Override
    public void publish(AppuserDeletedEvent event) {
        events.publishEvent(event);
    }

}
