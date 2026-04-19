package dev.ngb.scheduler;

import dev.ngb.application.ApplicationService;
import dev.ngb.application.BackgroundJob;
import dev.ngb.application.PublicApi;
import dev.ngb.application.UseCaseService;
import dev.ngb.constant.PackageConstants;
import dev.ngb.domain.DomainService;
import dev.ngb.domain.Repository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(
        basePackages = PackageConstants.BASE_PACKAGE,
        includeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {
                        UseCaseService.class,
                        ApplicationService.class,
                        DomainService.class,
                        Repository.class,
                        PublicApi.class,
                        BackgroundJob.class,
                }
        )
)
public class SchedulerApplication {
}
