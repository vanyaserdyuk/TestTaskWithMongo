package ru.testtask.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.testtask.dto.BackgroundJobDTO;
import ru.testtask.model.BackgroundJob;
import ru.testtask.model.BackgroundJobMongo;

@Repository
public interface BackgroundJobRepo extends MongoRepository<BackgroundJob, String> {
    BackgroundJobMongo getById(String id);
}
