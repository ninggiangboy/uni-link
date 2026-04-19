package dev.ngb.scheduler.config;

public interface ScheduleJobConfig {

    boolean attachmentPendingPutSweepEnabled();

    String attachmentPendingPutSweepCron();
}
