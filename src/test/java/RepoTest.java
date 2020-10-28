import com.mongodb.client.MongoClient;
import com.mongodb.client.internal.MongoClientImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.testtask.model.Attribute;
import ru.testtask.model.Geometry;
import ru.testtask.model.Project;
import ru.testtask.repo.ProjectRepo;
import java.util.Optional;
import java.util.UUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@DataMongoTest
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
