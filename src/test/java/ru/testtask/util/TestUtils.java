package ru.testtask.util;


import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.testtask.dto.UploadFileDTO;
import ru.testtask.model.FileData;
import ru.testtask.repo.FileDataRepo;
import ru.testtask.service.FileService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
public class TestUtils {

    @Value("${storage.root}")
    private String storageRoot;

    @Autowired
    private FileDataRepo fileDataRepo;

    @Autowired
    private FileService fileService;


    public void deleteAllFiles() throws IOException {
        FileUtils.cleanDirectory(Paths.get(storageRoot).toFile());
        fileDataRepo.deleteAll();
    }

    public FileData createTestData(){
        FileData fileData1 = FileData.builder().originalFilename("testFile")
                .filename("a")
                .directory("/")
                .build();
        return fileDataRepo.insert(fileData1);
    }

    public void createTestDataForRegexTest(){
        FileData fileData1 = FileData.builder().originalFilename("abcdefg")
                .filename("1")
                .directory("/dir1/dir2")
                .build();
        fileDataRepo.insert(fileData1);

        FileData fileData2 = FileData.builder().originalFilename("defabcggert")
                .filename("2")
                .directory("/dir2/dir3")
                .build();

        fileDataRepo.insert(fileData2);

        FileData fileData3 = FileData.builder().originalFilename("12345")
                .filename("3")
                .directory("/folder")
                .build();

        fileDataRepo.insert(fileData3);
    }

}
