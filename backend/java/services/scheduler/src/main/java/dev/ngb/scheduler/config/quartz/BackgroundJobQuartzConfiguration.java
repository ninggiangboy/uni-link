package dev.ngb.scheduler.config.quartz;

import dev.ngb.application.BackgroundJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Slf4j
public class BackgroundJobQuartzConfiguration {

    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(
            List<BackgroundJob> backgroundJobs,
            AutowireCapableBeanFactory beanFactory
    ) {
        SchedulerFactoryBean factoryBean = new SchedulerFactoryBean();
        factoryBean.setJobFactory(new AutowiringSpringBeanJobFactory(beanFactory));

        List<JobDetail> jobDetails = new ArrayList<>();
        List<Trigger> triggers = new ArrayList<>();
        for (BackgroundJob backgroundJob : backgroundJobs) {
            if (!backgroundJob.isEnabled()) {
                log.info("Background job disabled: {}", backgroundJob.jobName());
                continue;
            }

            JobDetail jobDetail = JobBuilder.newJob(GenericQuartzJob.class)
                    .withIdentity(backgroundJob.jobName(), "background-jobs")
                    .usingJobData(GenericQuartzJob.JOB_NAME, backgroundJob.jobName())
                    .storeDurably()
                    .build();

            Trigger trigger = TriggerBuilder.newTrigger()
                    .forJob(jobDetail)
                    .withIdentity(backgroundJob.jobName() + "_trigger")
                    .withSchedule(CronScheduleBuilder.cronSchedule(backgroundJob.cron()))
                    .build();

            jobDetails.add(jobDetail);
            triggers.add(trigger);
        }

        factoryBean.setJobDetails(jobDetails.toArray(new JobDetail[0]));
        factoryBean.setTriggers(triggers.toArray(new Trigger[0]));
        return factoryBean;
    }
}
