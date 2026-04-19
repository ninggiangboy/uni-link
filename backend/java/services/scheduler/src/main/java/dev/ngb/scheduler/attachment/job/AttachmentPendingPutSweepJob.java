package dev.ngb.scheduler.attachment.job;

import dev.ngb.application.BackgroundJob;
import dev.ngb.constant.ScheduledJobNames;
import dev.ngb.event.JobTriggeredEvent;
import dev.ngb.scheduler.config.ScheduleJobConfig;
import dev.ngb.scheduler.event.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AttachmentPendingPutSweepJob implements BackgroundJob {

    private final EventPublisher eventPublisher;
    private final ScheduleJobConfig scheduleJobConfig;

    @Override
    public void execute() {
        eventPublisher.publish(
                JobTriggeredEvent.create(ScheduledJobNames.ATTACHMENT_PENDING_PUT_SWEEP));
    }

    @Override
    public String cron() {
        return scheduleJobConfig.attachmentPendingPutSweepCron();
    }

    @Override
    public boolean isEnabled() {
        return scheduleJobConfig.attachmentPendingPutSweepEnabled();
    }

    @Override
    public String jobName() {
        return ScheduledJobNames.ATTACHMENT_PENDING_PUT_SWEEP;
    }
}
