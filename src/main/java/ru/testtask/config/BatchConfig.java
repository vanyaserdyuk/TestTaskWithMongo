package ru.testtask.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import ru.testtask.job.MyJobStep;
import ru.testtask.service.JobCompletionListener;

import javax.sql.DataSource;


@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Autowired
    private JobBuilderFactory jobs;

    @Autowired
    private StepBuilderFactory steps;

    @Bean
    public MyJobStep linesReader() {
        return new MyJobStep();
    }

    @Bean
    public JobCompletionListener jobCompletionListener() {
        return new JobCompletionListener();
    }

    @Bean
    protected Step doStep() {
        return steps
                .get("readLines")
                .tasklet(linesReader())
                .listener(jobCompletionListener())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Job job() {
        return jobs
                .get("myJob")
                .start(doStep())
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        SimpleAsyncTaskExecutor executor = new SimpleAsyncTaskExecutor();
        executor.setDaemon(true);
        executor.setThreadPriority(Thread.MIN_PRIORITY);
        executor.setThreadNamePrefix("MultiThreaded-");
        return executor;
    }
}
