package dev.ngb.application;

public interface BackgroundJob {

    void execute();

    String cron();

    default boolean isEnabled() {
        return true;
    }

    default String jobName() {
        return this.getClass().getName();
    }
}
