import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import ru.testtask.model.Attribute;
import ru.testtask.model.Geometry;
import ru.testtask.model.Project;
import ru.testtask.service.ProjectService;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(MockitoJUnitRunner.class)
public class ProjectServiceTest {

    private static ProjectService projectService;

    private static Project testProject;

    @Before
    public void buildTestProject(){
        testProject = new Project();
        testProject.setName("Test");
    }

    @Before
    public void initService(){
         projectService = new ProjectService();
    }

    @Test
    public void createTest(){
        Project resultProject = projectService.createProject(testProject);
        assertNotNull(resultProject);
        assertEquals(10, resultProject.getAttributes().size());
        assertEquals(10, resultProject.getGeometries().size());
        List<Geometry> testGem = resultProject.getGeometries();
        for (Geometry geometry : testGem){
            assertNotNull(geometry.getName());
            assertNotNull(geometry.getId());
        }
        List<Attribute> testAttr = resultProject.getAttributes();
        for (Attribute attribute : testAttr){
            assertNotNull(attribute.getName());
            assertNotNull(attribute.getId());
        }
    }
}
