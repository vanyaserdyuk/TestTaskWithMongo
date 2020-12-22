package ru.testtask.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import ru.testtask.dto.BackgroundJobDTO;
import ru.testtask.model.BackgroundJob;
import ru.testtask.model.BackgroundJobStatus;
import ru.testtask.repo.BackgroundJobRepo;

import javax.annotation.PostConstruct;
import java.util.ArrayDeque;

@Service
@Slf4j
public class BackgroundJobService {
    @Autowired
    private BackgroundJobRepo backgroundJobRepo;

    private final ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

    private final ArrayDeque<BackgroundJob> backgroundJobs = new ArrayDeque<>();

    @PostConstruct
    public void init(){
        threadPoolTaskScheduler.setDaemon(true);
        threadPoolTaskScheduler.initialize();
    }

    public void startJob(){
        threadPoolTaskScheduler.execute(backgroundJobs.getFirst().getJobExecution());
    }

    public void saveJob(BackgroundJob backgroundJob) {
        backgroundJob.setJobStatus(BackgroundJobStatus.IN_PROGRESS);
        backgroundJobs.add(backgroundJob);
        backgroundJobRepo.save(backgroundJob);
    }

    public void createJob(String name){
        BackgroundJob backgroundJob = BackgroundJob.builder().name(name).build();
        backgroundJob.setRunning(true);
        backgroundJob.setJobExecution(() -> {
            for (int i = 0; i < 10; i++) {
                if (backgroundJob.isRunning()) {
                    try {
                        System.out.print("a ");
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        backgroundJob.setJobStatus(BackgroundJobStatus.FAILED);
                        log.error("Thread was interrupted");
                    }
                    backgroundJob.setProgress(backgroundJob.getProgress() + 10);
                }
            }
            backgroundJob.setJobStatus(BackgroundJobStatus.COMPLETED);
        });
        saveJob(backgroundJob);
        startJob();
    }

    public void cancelJob(){
        backgroundJobs.getFirst().setRunning(false);
    }

    public void deleteJob(String jobName){
        backgroundJobs.removeIf(backgroundJob1 -> backgroundJob1.getName().equals(jobName));
    }

    public BackgroundJobDTO getJobStatusAndProgress(){
        return BackgroundJobDTO.builder().jobStatus(backgroundJobs.getFirst().getJobStatus())
                .progress(backgroundJobs.getFirst().getProgress()).build();
    }

}
