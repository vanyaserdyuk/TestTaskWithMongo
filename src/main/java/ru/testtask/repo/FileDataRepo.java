package ru.testtask.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import ru.testtask.model.FileData;

import java.nio.file.Path;

@Repository
public interface FileDataRepo extends MongoRepository<FileData, String> {
    FileData findByFilename(String filename);
    void deleteByFilename(String filename);
}
