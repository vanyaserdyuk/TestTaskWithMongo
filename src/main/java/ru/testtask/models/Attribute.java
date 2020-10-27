package ru.testtask.models;

import org.springframework.data.mongodb.core.mapping.Document;

public class Attribute {
    private int id;
    private Project project;
    private String name;

    public Attribute(){

    }

    public void setProject(Project project) {
        this.project = project;
    }

    public void setName(String name) {
        this.name = name;
    }
}
