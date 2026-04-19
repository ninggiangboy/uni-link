package dev.ngb.scheduler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public record ScheduleJobPropertiesConfig(
        @Value("${scheduler.jobs.attachment-pending-put-sweep.cron:0 */15 * * * ?}")
        String attachmentPendingPutSweepCron,

        @Value("${scheduler.jobs.attachment-pending-put-sweep.enabled:true}")
        boolean attachmentPendingPutSweepEnabled
) implements ScheduleJobConfig {}
