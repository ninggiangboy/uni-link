package dev.ngb.scheduler.config.quartz;

import dev.ngb.application.BackgroundJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class GenericQuartzJob implements Job {

    public static final String JOB_NAME = "JOB_NAME";

    private final Map<String, BackgroundJob> jobsByName;

    public GenericQuartzJob(List<BackgroundJob> backgroundJobs) {
        this.jobsByName = new HashMap<>();
        for (BackgroundJob backgroundJob : backgroundJobs) {
            BackgroundJob previous = jobsByName.put(backgroundJob.jobName(), backgroundJob);
            if (previous != null) {
                throw new IllegalStateException("Duplicate background job name: " + backgroundJob.jobName());
            }
        }
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            String jobName = context.getMergedJobDataMap().getString(JOB_NAME);
            BackgroundJob backgroundJob = jobsByName.get(jobName);
            if (backgroundJob == null) {
                throw new IllegalArgumentException("Background job not found: " + jobName);
            }
            try {
                backgroundJob.execute();
            } catch (RuntimeException ex) {
                log.warn("Background operation failed: {}", backgroundJob.jobName(), ex);
                throw new JobExecutionException(ex, false);
            }
        } catch (Exception ex) {
            throw new JobExecutionException("Failed to execute generic background job", ex);
        }
    }
}
