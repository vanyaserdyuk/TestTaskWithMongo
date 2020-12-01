package ru.testtask.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ru.testtask.Application;
import ru.testtask.config.TestConfig;
import ru.testtask.dto.UploadFileDTO;
import ru.testtask.model.FileData;

import java.nio.file.Files;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {Application.class})
@ContextConfiguration(classes = {TestConfig.class})
@ActiveProfiles("test")
public class FileServiceTest {

    @Autowired
    private FileService fileService;

    @Before
    public void buildTestData(){
        FileData fileData = FileData.builder().originalFilename("a")
                .filename("b")
                .size(1)
                .type("png")
                .directory("dir")
                .build();
    }

    @Test
    public void addFileTest(){
        UploadFileDTO uploadFileDTO = UploadFileDTO.builder().fileUrl("1").fileName("b").build();
        FileData fileData = fileService.addFile(uploadFileDTO);
        assertNotNull(fileData.getFilename());
        assertEquals("b", fileData.getOriginalFilename());

    }


}
