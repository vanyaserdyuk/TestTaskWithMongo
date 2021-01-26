package ru.testtask.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancelJob(@PathVariable String id){
        backgroundJobService.cancelJob(id);
        return new ResponseEntity<>(String.format("The job with id %s has been cancelled", id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJob(@PathVariable String id){
        try {
            backgroundJobService.deleteJob(id);
        }
        catch (RuntimeException e){
            return new ResponseEntity<>("Running background job can't be deleted"
                    , HttpStatus.OK);
        }
        return new ResponseEntity<>("The job has been deleted", HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJobProgressAndStatus(@PathVariable String id){
        BackgroundJobDTO backgroundJobDTO;
        try {
            backgroundJobDTO = backgroundJobService.getJobStatusAndProgress(id);
        }
        catch (NullPointerException e){
            return new ResponseEntity<>(String.format("Job with ID %s does not found", id), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(backgroundJobDTO, HttpStatus.OK);
    }
}
