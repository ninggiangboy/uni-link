package dev.ngb.scheduler.profile.job;

import dev.ngb.application.BackgroundJob;
import dev.ngb.application.port.event.EventPublisher;
import dev.ngb.constant.ScheduledJobNames;
import dev.ngb.event.JobTriggeredEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FollowerCountFlushJob implements BackgroundJob {

    private static final String CRON = "*/5 * * * * ?";

    private final EventPublisher eventPublisher;

    @Override
    public void execute() {
        eventPublisher.publish(
                JobTriggeredEvent.create(ScheduledJobNames.FOLLOWER_COUNT_FLUSH));
    }

    @Override
    public String cron() {
        return CRON;
    }

    @Override
    public String jobName() {
        return ScheduledJobNames.FOLLOWER_COUNT_FLUSH;
    }
}
