package ru.testtask.models;

public class Geometry {
    private int id;
    private Project project;
    private String name;

    public Geometry(){
    }

    public void setProject(Project project) {
        this.project = project;
    }
    public void setName(String name) {
        this.name = name;
    }
}
