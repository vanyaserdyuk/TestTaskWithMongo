package ru.testtask.repo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.internal.MongoClientImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.testtask.Application;
import ru.testtask.model.Attribute;
import ru.testtask.model.Geometry;
import ru.testtask.model.Project;
import ru.testtask.repo.ProjectRepo;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@AutoConfigureDataMongo
@SpringBootTest(classes = {Application.class})
public class RepoTest {

    @Autowired
    private ProjectRepo projectRepo;

    Project project;

    @Before
    public void buildTestData(){
        project = new Project();
        project.setName("Test");
        project.setId("1");
        for (int i = 0; i < 10; i++) {
            project.addGeometry(Geometry.builder().name("geometry" + i).id(UUID.randomUUID().toString()).build());
            project.addAttribute(Attribute.builder().name("geometry" + i).id(UUID.randomUUID().toString()).build());
        }
        projectRepo.save(project);
    }

    @Test
    public void checkId(){
        assertNotNull(projectRepo.findAll());
        assertNotNull(projectRepo.findByName("Test"));
        assertEquals("1", projectRepo.findByName("Test").getId());
    }

    @Test
    public void checkFindingByName(){
        Project prj = projectRepo.findByName("Test");
        assertNotNull(prj);
        assertEquals(prj.getName(), project.getName());
    }

    @Test
    public void checkFindingById(){
        Optional<Project> optional = projectRepo.findById(project.getId());
        Project prj = optional.get();
        assertEquals(prj.getId(), project.getId());
    }

    @Test
    public void deleteTest(){
        projectRepo.deleteById("1");
        Optional<Project> optional = projectRepo.findById("1");
        assertTrue(optional.isEmpty());
    }

}
