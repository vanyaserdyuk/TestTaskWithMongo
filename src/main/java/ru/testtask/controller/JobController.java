package ru.testtask.controller;


import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.testtask.model.JobData;
import ru.testtask.repo.JobDataRepo;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Set;

@RestController
@RequestMapping("/api/jobs")
@Slf4j
public class JobController {

    @Autowired
    private JobRegistry jobRegistry;

    final JobLauncher jobLauncher;

    final Job processJob;

    final JobOperator jobOperator;

    final JobRepository jobRepository;

    private JobExecution jobExecution;

    private JobParameters jobParameters;

    @Autowired
    private JobDataRepo jobDataRepo;

    public JobController(JobLauncher jobLauncher, Job processJob, JobOperator jobOperator, JobRepository jobRepository) {
        this.jobLauncher = jobLauncher;
        this.processJob = processJob;
        this.jobOperator = jobOperator;
        this.jobRepository = jobRepository;
    }

    @PostConstruct
    private void init(){
        jobParameters = new JobParametersBuilder().addLong("time", System.currentTimeMillis())
                .toJobParameters();
    }

    @RequestMapping("/invokejob")
    public String handle() throws Exception {
        jobLauncher.run(processJob, jobParameters);
        try {
            jobExecution = jobRepository.createJobExecution(processJob.getName(), jobParameters);

        }
        catch (Exception e){
            log.error("Something went wrong while creating JobExecution", e);
        }

        return "Batch job has been invoked";
    }

    @RequestMapping("/cancelJob")
    public String cancel() throws Exception {
        jobOperator.stop(jobExecution.getJobId());

        return "Batch job has been stopped";
    }

    @RequestMapping("/deleteJob")
    public String delete() throws Exception {
        jobOperator.abandon(jobExecution.getJobId());
        return "Batch job has been abandoned";
    }

    @RequestMapping("/getJobStatus")
    public String getStatusAndProgress(){
        long progress;
        BatchStatus status = jobExecution.getStatus();
        try {
            progress = ((jobExecution.getEndTime().getTime() - new Date().getTime()) /
                    (jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime())) * 100;
        }
        catch (NullPointerException e){
            progress = 0;
        }
        JobData jobData = JobData.builder().progress(progress).status(status.toString()).name(processJob.getName()).build();
        jobDataRepo.insert(jobData);
        return status.toString();
    }


}
