package ru.testtask.models;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.testtask.services.ProjectService;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "projects")
public class Project {

    @Id
    private String id;
    private long seq;
    private String name;
    private List<Attribute> attributes;
    private List<Geometry> geometries;

    public Project(){
    }

    public Project(String name){
        this.name = name;
        this.attributes = new ArrayList<>();
        this.geometries = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Geometry geometry = new Geometry();
            Attribute attribute = new Attribute();
//            geometry.setProject(this);
//            attribute.setProject(this);
            geometry.setName("geometry" + i);
            attribute.setName("attribute" + i);
            this.attributes.add(attribute);
            this.geometries.add(geometry);
        }
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
}
