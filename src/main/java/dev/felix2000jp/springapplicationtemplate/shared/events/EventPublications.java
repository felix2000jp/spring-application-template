package dev.felix2000jp.springapplicationtemplate.shared.events;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.modulith.events.CompletedEventPublications;
import org.springframework.modulith.events.IncompleteEventPublications;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;

@EnableScheduling
@Component
class EventPublications {

    @Value("${events.schedule.complete-event-older-than-in-minutes}")
    private int completeEventOlderThanInMinutes;

    @Value("${events.schedule.incomplete-event-older-than-in-minutes}")
    private int incompleteEventOlderThanInMinutes;

    private final CompletedEventPublications completeEvents;
    private final IncompleteEventPublications incompleteEvents;

    EventPublications(CompletedEventPublications completeEvents, IncompleteEventPublications incompleteEvents) {
        this.completeEvents = completeEvents;
        this.incompleteEvents = incompleteEvents;
    }

    @Scheduled(cron = "${events.schedule.complete-event-cron-job}")
    void clearCompletedEventPublications() {
        var duration = Duration.ofMinutes(completeEventOlderThanInMinutes);
        completeEvents.deletePublicationsOlderThan(duration);
    }

    @Scheduled(cron = "${events.schedule.incomplete-event-cron-job}")
    void resubmitFailedEventPublications() {
        var duration = Duration.ofMinutes(incompleteEventOlderThanInMinutes);
        incompleteEvents.resubmitIncompletePublicationsOlderThan(duration);
    }

}
