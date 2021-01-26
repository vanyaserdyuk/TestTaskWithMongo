package ru.testtask.service;


import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import ru.testtask.dto.BackgroundJobDTO;
import ru.testtask.model.BackgroundJob;
import ru.testtask.model.BackgroundJobStatus;
import ru.testtask.repo.BackgroundJobRepo;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class BackgroundJobService {
    @Autowired
    private BackgroundJobRepo backgroundJobRepo;

    private final ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

    private final Map<String, BackgroundJob> runningBackgroundJobs = new ConcurrentHashMap<>();

    private final int jobIterations = 20;

    @PostConstruct
    public void init(){
        threadPoolTaskScheduler.setDaemon(true);
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.initialize();
    }

    public void startJob(BackgroundJob job){
      threadPoolTaskScheduler.execute(Objects.requireNonNull(job.getJobExecution()));
    }

    public void saveJob(BackgroundJob backgroundJob) {
        backgroundJob.setJobStatus(BackgroundJobStatus.IN_PROGRESS);
        backgroundJobRepo.save(backgroundJob);
        runningBackgroundJobs.put(backgroundJob.getId(), backgroundJob);
    }

    public void createJob(String name) {
        BackgroundJob backgroundJob = BackgroundJob.builder().name(name).build();
        backgroundJob.setJobExecution(() -> {
            log.info("Background job with name {} and id {} started", backgroundJob.getName(), backgroundJob.getId());
            for (int i = 0; i < jobIterations; i++) {
                if (!backgroundJob.getJobStatus().equals(BackgroundJobStatus.IN_PROGRESS))
                    break;
                try {
//                    System.out.print("a ");
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    backgroundJob.setJobStatus(BackgroundJobStatus.FAILED);
                    log.error("Thread was interrupted");
                }
                backgroundJob.setProgress(backgroundJob.getProgress() + 100 / jobIterations);
                backgroundJobRepo.save(backgroundJob);
            }
            if (backgroundJob.getProgress() == 100)
                backgroundJob.setJobStatus(BackgroundJobStatus.COMPLETED);
            runningBackgroundJobs.remove(backgroundJob);
            backgroundJobRepo.save(backgroundJob);
            log.info("Background job with name {} and id {} is completed", backgroundJob.getName(), backgroundJob.getId());
        });
        saveJob(backgroundJob);
        startJob(backgroundJob);
    }

    public void cancelJob(String id){
        BackgroundJob backgroundJob = runningBackgroundJobs.get(id);
        if (backgroundJob != null) {
            backgroundJob.setJobStatus(BackgroundJobStatus.CANCELLED);
            backgroundJobRepo.save(backgroundJob);
        }
    }

    public void deleteJob(String id) throws RuntimeException{
        if (runningBackgroundJobs.get(id) != null){
            throw new RuntimeException("Running background job can't be deleted");
        }
        else {
            backgroundJobRepo.deleteById(id);
        }
    }

    public BackgroundJobDTO getJobStatusAndProgress(String id) throws NullPointerException {
        BackgroundJob backgroundJob = findBackgroundJobById(id);

        return BackgroundJobDTO.builder().jobStatus(backgroundJob.getJobStatus())
                .progress(backgroundJob.getProgress()).id(backgroundJob.getId()).build();
    }

    private BackgroundJob findBackgroundJobById(String id) throws NullPointerException{
        BackgroundJob backgroundJob = null;
            Optional<BackgroundJob> optionalBackgroundJob = backgroundJobRepo.findById(id);
            if (optionalBackgroundJob.isPresent())
                backgroundJob = optionalBackgroundJob.get();
        if (backgroundJob == null){
            throw new NullPointerException("This job doesn not exist");
        }
        return backgroundJob;
    }

}
