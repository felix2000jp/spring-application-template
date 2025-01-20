package dev.felix2000jp.springapplicationtemplate.auth.application;

import dev.felix2000jp.springapplicationtemplate.auth.application.events.AppuserDeletedEvent;

public interface AppuserPublisher {

    void publish(AppuserDeletedEvent event);

}
