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
import java.util.Optional;

@Service
@Slf4j
public class BackgroundJobService {
    @Autowired
    private BackgroundJobRepo backgroundJobRepo;

    private final ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

    private final ArrayDeque<BackgroundJob> backgroundJobs = new ArrayDeque<>();

    private BackgroundJob currentBackgroundJob;

    @PostConstruct
    public void init(){
        threadPoolTaskScheduler.setDaemon(true);
        threadPoolTaskScheduler.initialize();
    }

    public void startJob(){
        currentBackgroundJob = backgroundJobs.pop();
        threadPoolTaskScheduler.execute(currentBackgroundJob.getJobExecution());
    }

    public void saveJob(BackgroundJob backgroundJob) {
        backgroundJob.setJobStatus(BackgroundJobStatus.IN_PROGRESS);
        backgroundJobs.add(backgroundJob);
        backgroundJobRepo.save(backgroundJob);
    }

    public void createJob(String name){
        BackgroundJob backgroundJob = BackgroundJob.builder().name(name).build();
        backgroundJob.setJobExecution(() -> {
            for (int i = 0; i < 10; i++) {
                if (backgroundJob.getJobStatus().equals(BackgroundJobStatus.IN_PROGRESS)) {
                    log.debug("Background job with name {} started", backgroundJob.getName());
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

    public void cancelJob(String id){
        BackgroundJob backgroundJob = findBackgroundJobById(id);
        backgroundJob.setJobStatus(BackgroundJobStatus.CANCELLED);
        backgroundJobRepo.save(backgroundJob);
    }

    public void deleteJob(String id){
        backgroundJobs.removeIf(backgroundJob1 -> backgroundJob1.getId().equals(id));
        backgroundJobRepo.deleteById(id);
    }

    public BackgroundJobDTO getJobStatusAndProgress(String id) throws NullPointerException {
        BackgroundJob backgroundJob = findBackgroundJobById(id);

        return BackgroundJobDTO.builder().jobStatus(backgroundJob.getJobStatus())
                .progress(backgroundJob.getProgress()).build();
    }

    private BackgroundJob findBackgroundJobById(String id) throws NullPointerException{
        BackgroundJob backgroundJob = null;
        if (id.equals(currentBackgroundJob.getId())) {
            backgroundJob = currentBackgroundJob;
        }
        else {
            Optional<BackgroundJob> optionalBackgroundJob = backgroundJobRepo.findById(id);
            if (optionalBackgroundJob.isPresent())
                backgroundJob = optionalBackgroundJob.get();
        }
        if (backgroundJob == null){
            throw new NullPointerException("This job doesn not exist");
        }
        return backgroundJob;
    }

}
