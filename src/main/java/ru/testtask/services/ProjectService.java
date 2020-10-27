package ru.testtask.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.testtask.DAO.ProjectDAO;
import ru.testtask.aspects.TrackExecutionTime;
import ru.testtask.models.Project;

import java.util.List;

@Component
public class ProjectService {
    @Autowired
    private ProjectDAO projectDao = new ProjectDAO();

    public ProjectService() {
    }

    @TrackExecutionTime
    public Project findProject(int id) {
        return projectDao.findById(id);
    }

    @TrackExecutionTime
    public void saveProject(Project project) {
        projectDao.save(project);
    }

    @TrackExecutionTime
    public void deleteProject(Project project) {
        projectDao.delete(project);
    }

    @TrackExecutionTime
    public void updateProject(Project project) {
        projectDao.update(project);
    }

    @TrackExecutionTime
    public List<Project> findAllUsers() {
        return projectDao.findAll();
    }


}
