package ru.testtask.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.testtask.dto.BackgroundJobDTO;
import ru.testtask.service.BackgroundJobService;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private BackgroundJobService backgroundJobService;

    @GetMapping("/start")
    public ResponseEntity<String> startJob(@RequestParam String jobName){
        backgroundJobService.createJob(jobName);

      return new ResponseEntity<>("The job has been started", HttpStatus.OK);
    }

    @GetMapping("/cancel")
    public ResponseEntity<String> cancelJob(){
        backgroundJobService.cancelJob();
        return new ResponseEntity<>("The job has been cancelled", HttpStatus.OK);
    }

    @GetMapping("/delete")
    public ResponseEntity<String> deleteJob(@RequestParam String jobName){
        backgroundJobService.deleteJob(jobName);
        return new ResponseEntity<>("The job has been deleted", HttpStatus.OK);
    }

    @GetMapping("/info")
    public ResponseEntity<String> getJobProgressAndStatus(){
        BackgroundJobDTO backgroundJobDTO = backgroundJobService.getJobStatusAndProgress();
        return new ResponseEntity<>(String.format("The job progress is %d percent and status is %s", backgroundJobDTO.getProgress(),
                backgroundJobDTO.getJobStatus().toString()), HttpStatus.OK);
    }





}
