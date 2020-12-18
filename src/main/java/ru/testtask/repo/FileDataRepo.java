package ru.testtask.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import ru.testtask.model.FileData;


import java.util.List;

@Repository
public interface FileDataRepo extends MongoRepository<FileData, String> {
    FileData findByFilename(String filename);

    @Query("{ 'originalFilename' : { $regex: ?0 } }")
    List<FileData> findFileDataByRegexpFilename(String regexp);

    @Query("{ 'directory' : { $regex: ?0 } }")
    List<FileData> findFileDataByRegexpDirectory(String regexp);
}
