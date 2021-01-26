package ru.testtask.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.testtask.model.BackgroundJob;

@Repository
public interface BackgroundJobRepo extends MongoRepository<BackgroundJob, String> {

}
