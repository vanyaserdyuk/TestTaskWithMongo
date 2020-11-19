package ru.testtask.repo;

import com.mongodb.client.MongoClient;
import com.mongodb.client.internal.MongoClientImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.data.mongo.AutoConfigureDataMongo;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ru.testtask.Application;
import ru.testtask.config.TestConfig;
import ru.testtask.model.Attribute;
import ru.testtask.model.Geometry;
import ru.testtask.model.Project;
import ru.testtask.repo.ProjectRepo;
import java.util.Optional;
import java.util.UUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@AutoConfigureDataMongo
@SpringBootTest(classes = {Application.class})
@ContextConfiguration(classes = {TestConfig.class})
@ActiveProfiles("test")
public class RepoTest {

    ProjectRepo projectRepo;

    Project project;

    @Before
    public void buildTestData(){
        project = new Project();
        project.setName("Test");
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
    }

    @Test
    public void checkFindingByName(){
        Project prj = projectRepo.findByName("Test");
        assertEquals(prj.getName(), project.getName());
    }

    @Test
    public void checkFindingById(){
        Optional<Project> optional = projectRepo.findById(project.getId());
        Project prj = optional.get();
        assertEquals(prj.getId(), project.getId());
    }
}
