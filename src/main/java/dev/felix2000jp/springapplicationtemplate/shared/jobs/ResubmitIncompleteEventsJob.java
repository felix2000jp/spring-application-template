package dev.felix2000jp.springapplicationtemplate.shared.jobs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.modulith.events.IncompleteEventPublications;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@EnableScheduling
class ResubmitIncompleteEventsJob {

    @Value("${events.schedule.incomplete-event-older-than-in-minutes}")
    private int incompleteEventOlderThanInMinutes;

    private final IncompleteEventPublications incompleteEvents;

    ResubmitIncompleteEventsJob(IncompleteEventPublications incompleteEvents) {
        this.incompleteEvents = incompleteEvents;
    }

    @Scheduled(cron = "${events.schedule.incomplete-event-cron-job}")
    void resubmitIncompleteEvents() {
        var duration = Duration.ofMinutes(incompleteEventOlderThanInMinutes);
        incompleteEvents.resubmitIncompletePublicationsOlderThan(duration);
    }

}
