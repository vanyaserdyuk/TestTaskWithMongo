package ru.testtask.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.testtask.dto.BackgroundJobDTO;
import ru.testtask.dto.UserDTO;
import ru.testtask.model.BackgroundJob;
import ru.testtask.service.BackgroundJobService;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    @Autowired
    private BackgroundJobService backgroundJobService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/start")
    public ResponseEntity<BackgroundJobDTO> startJob(@RequestParam String jobName){
        BackgroundJob backgroundJob = backgroundJobService.createJob(jobName);
        return new ResponseEntity<>(modelMapper.map(backgroundJob, BackgroundJobDTO.class), HttpStatus.CREATED);
      //return ResponseEntity.created(URI.create(String.format("/api/jobs/%s", backgroundJob.getId()))).build();
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<String> cancelJob(@PathVariable String id){
        backgroundJobService.cancelJob(id);
        return new ResponseEntity<>(String.format("The job with id %s has been cancelled", id), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteJob(@PathVariable String id) {
        try {
            backgroundJobService.deleteJob(id);
        } catch (RuntimeException e) {
            return new ResponseEntity<>("Running background job can't be deleted"
                    , HttpStatus.UNPROCESSABLE_ENTITY);
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

    @GetMapping
    public ResponseEntity<List<BackgroundJobDTO>> getAllJobs(){
        List<BackgroundJob> backgroundJobs = backgroundJobService.getAllJobs();
        List<BackgroundJobDTO> backgroundJobDTOList = backgroundJobs.stream().map(user -> modelMapper.map(user, BackgroundJobDTO.class)).collect(Collectors.toList());
        return new ResponseEntity<>(backgroundJobDTOList, HttpStatus.OK);
    }
}
