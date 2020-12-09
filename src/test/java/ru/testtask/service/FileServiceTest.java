package ru.testtask.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ru.testtask.Application;
import ru.testtask.config.TestConfig;
import ru.testtask.dto.UploadFileDTO;
import ru.testtask.model.FileData;
import ru.testtask.util.TestUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@ContextConfiguration(classes = {TestConfig.class})
@ActiveProfiles("test")
public class FileServiceTest {

    private final static String testUrl = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/Sunset02.jpg/220px-Sunset02.jpg";

    @Autowired
    private FileService fileService;

    @Autowired
    private TestUtils testUtils;

    @After
    public void clearDb() throws IOException {
        testUtils.deleteAllFiles();
    }

    @Test
    public void addFileTest(){
        UploadFileDTO uploadFileDTO = UploadFileDTO.builder().fileUrl(testUrl)
                .fileName("b").build();
        FileData fileData = fileService.uploadFile(uploadFileDTO);
        assertNotNull(fileData.getFilename());
        assertEquals("b", fileData.getOriginalFilename());
        assertEquals(fileService.getStorageRootPath().toString(), fileData.getDirectory());
        assertEquals("image/jpeg", fileData.getType());
        assertNotNull(fileData.getId());
        assertEquals(10158, fileData.getSize());

    }

    @Test
    public void removeFileTest() throws FileNotFoundException {
        FileData fileData = testUtils.createTestData();
        fileService.removeFile(fileData.getId());
        assertEquals(Optional.empty(), fileService.findFileById(fileData.getId()));
        assertTrue(Files.exists(fileService.getStorageRootPath().resolve(fileData.getFilename())));
    }

    @Test
    public void moveFileTest() throws FileNotFoundException {
        UploadFileDTO uploadFileDTO = UploadFileDTO.builder().fileUrl(testUrl)
                .fileName("b").build();
        FileData fileData = fileService.uploadFile(uploadFileDTO);
        fileData = fileService.moveFile(fileData.getId(), "a");
        assertFalse(Files.exists(Paths.get(fileService.getStorageRootPath().toString() + File.separator + fileData.getFilename())));
        assertTrue(Files.exists(Paths.get(fileService.getStorageRootPath().toString() + File.separator +
                "a" + File.separator + fileData.getFilename())));
        assertEquals("C:\\Files\\a", fileData.getDirectory());
    }

    @Test
    public void copyFileTest() throws IOException {
        UploadFileDTO uploadFileDTO = UploadFileDTO.builder().fileUrl(testUrl)
                .fileName("test").build();
        FileData fileData = fileService.uploadFile(uploadFileDTO);
        fileService.copyFile(fileData.getId(), "dir1");
        assertTrue(Files.exists(Paths.get(fileService.getStorageRootPath().toString() + File.separator + "dir1")));
        assertTrue(Files.exists(Paths.get(fileService.getStorageRootPath().toString() + File.separator + "dir1" +
                File.separator + fileData.getFilename())));
        assertEquals(2, fileService.searchByRegex("test").size());
    }

    @Test
    public void findByRegexpTest(){
        testUtils.createTestDataForRegexTest();

        List<FileData> fileDataList = fileService.searchByRegex("^abc");
        assertEquals(1, fileDataList.size());
        assertEquals("abcdefg", fileDataList.get(0).getOriginalFilename());

        fileDataList = fileService.searchByRegex("abc");
        assertEquals(2, fileDataList.size());

        fileDataList = fileService.searchByRegex("\\d");
        assertEquals(1, fileDataList.size());
        assertEquals("12345", fileDataList.get(0).getOriginalFilename());

        fileDataList = fileService.searchByRegex(".");
        assertEquals(3, fileDataList.size());
    }


    @Test
    public void findByDirectoryTest(){
        testUtils.createTestDataForRegexTest();

        List<FileData> fileDataList = fileService.getFileListFromDirectory("dir1");
        assertEquals(1, fileDataList.size());
        assertEquals("abcdefg", fileDataList.get(0).getOriginalFilename());

        fileDataList = fileService.getFileListFromDirectory("dir2");
        assertEquals(2, fileDataList.size());

        fileDataList = fileService.getFileListFromDirectory("dir3");
        assertEquals(1, fileDataList.size());
        assertEquals("defabcggert", fileDataList.get(0).getOriginalFilename());

        fileDataList = fileService.getFileListFromDirectory("dir");
        assertEquals(2, fileDataList.size());

        fileDataList = fileService.getFileListFromDirectory("folder");
        assertEquals(1, fileDataList.size());
    }

}
