package ru.testtask.repo;


import org.springframework.batch.core.Job;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.testtask.model.JobData;

@Repository
public interface JobDataRepo extends MongoRepository<JobData, String> {
}
