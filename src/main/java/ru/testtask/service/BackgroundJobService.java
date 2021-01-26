package ru.testtask.service;


import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import ru.testtask.dto.BackgroundJobDTO;
import ru.testtask.model.BackgroundJob;
import ru.testtask.model.BackgroundJobMongo;
import ru.testtask.model.BackgroundJobStatus;
import ru.testtask.repo.BackgroundJobRepo;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class BackgroundJobService {
    @Autowired
    private BackgroundJobRepo backgroundJobRepo;

    private final ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

    private final List<BackgroundJob> runningBackgroundJobs = new ArrayList<>();

    private final int jobIterations = 20;

    @PostConstruct
    public void init(){
        threadPoolTaskScheduler.setDaemon(true);
        threadPoolTaskScheduler.setPoolSize(5);
        threadPoolTaskScheduler.initialize();
    }

    public void startJob(){
      threadPoolTaskScheduler.execute(Objects.requireNonNull(runningBackgroundJobs.get(runningBackgroundJobs.size() - 1)
              .getJobExecution()));
    }

    public void saveJob(BackgroundJob backgroundJob) {
        backgroundJob.setJobStatus(BackgroundJobStatus.IN_PROGRESS);
        backgroundJobRepo.save(backgroundJob);
        runningBackgroundJobs.add(backgroundJob);
    }

    public void createJob(String name){
        BackgroundJob backgroundJob = BackgroundJob.builder().name(name).build();
        backgroundJob.setJobExecution(() -> {
            log.debug("Background job with name {} started", backgroundJob.getName());
            for (int i = 0; i < jobIterations; i++) {
                if (backgroundJob.getJobStatus().equals(BackgroundJobStatus.IN_PROGRESS)) {
                    try {
                        System.out.print("a ");
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        backgroundJob.setJobStatus(BackgroundJobStatus.FAILED);
                        log.error("Thread was interrupted");
                    }
                    backgroundJob.setProgress(backgroundJob.getProgress() + 100/jobIterations);
                    backgroundJobRepo.save(backgroundJob);
                }
            }
            if (backgroundJob.getProgress() == 100)
            backgroundJob.setJobStatus(BackgroundJobStatus.COMPLETED);
            runningBackgroundJobs.remove(backgroundJob);
            backgroundJobRepo.save(backgroundJob);
        });
        saveJob(backgroundJob);
        startJob();
    }

    public void cancelJob(String id){
        BackgroundJob backgroundJob = runningBackgroundJobs.stream().filter(backgroundJob1 ->
                id.equals(backgroundJob1.getId())).findAny().orElse(null);
        if (backgroundJob != null) {
            runningBackgroundJobs.stream()
                    .filter(backgroundJob1 -> id.equals(backgroundJob1.getId()))
                    .forEach(backgroundJob1 -> backgroundJob1.setJobStatus(BackgroundJobStatus.CANCELLED));
            backgroundJob.setJobStatus(BackgroundJobStatus.CANCELLED);
            backgroundJobRepo.save(backgroundJob);
        }
    }

    public void deleteJob(String id) throws RuntimeException{
        if (runningBackgroundJobs.stream().filter(backgroundJob1 ->
                id.equals(backgroundJob1.getId())).findAny().orElse(null) != null){
            throw new RuntimeException("Running background job can't be deleted");
        }
        else {
            backgroundJobRepo.deleteById(id);
        }
    }

    public BackgroundJobDTO getJobStatusAndProgress(String id) throws NullPointerException {
        BackgroundJobMongo backgroundJob = findBackgroundJobById(id);

        return BackgroundJobDTO.builder().jobStatus(backgroundJob.getJobStatus())
                .progress(backgroundJob.getProgress()).build();
    }

    private BackgroundJobMongo findBackgroundJobById(String id) throws NullPointerException{
            BackgroundJobMongo backgroundJobMongo = backgroundJobRepo.getById(id);
        if (backgroundJobMongo == null){
            throw new NullPointerException("This job does not exist");
        }
        return backgroundJobMongo;
    }

}
