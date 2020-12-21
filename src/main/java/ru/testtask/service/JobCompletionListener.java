package ru.testtask.service;

import org.springframework.batch.core.listener.JobExecutionListenerSupport;

import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.JobExecution;

public class JobCompletionListener extends JobExecutionListenerSupport {

    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getBatchStatus() == BatchStatus.COMPLETED) {
            System.out.println("BATCH JOB COMPLETED SUCCESSFULLY");
        }
    }
}
