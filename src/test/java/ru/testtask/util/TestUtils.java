package ru.testtask.util;


import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.testtask.repo.FileDataRepo;

import java.io.IOException;
import java.nio.file.Paths;

@Service
public class TestUtils {

    @Value("${storage.root}")
    private String storageRoot;

    @Autowired
    private FileDataRepo fileDataRepo;


    public void deleteAllFiles() throws IOException {
        FileUtils.cleanDirectory(Paths.get(storageRoot).toFile());
        fileDataRepo.deleteAll();
    }

}
