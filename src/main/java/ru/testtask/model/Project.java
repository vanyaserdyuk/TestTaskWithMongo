package ru.testtask.model;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "projects")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Project {

    @Id
    @Getter
    @Setter
    private String id;

    @Setter
    @Getter
    private String name;

    @Getter
    @Setter
    private String ownerId;

    @Getter
    @Setter
    private List<Attribute> attributes = new ArrayList<>();
    @Getter
    private List<Geometry> geometries = new ArrayList<>();


    public void addGeometry(Geometry geometry){
        this.geometries.add(geometry);
    }

    public void addAttribute(Attribute attribute){
        this.attributes.add(attribute);
    }
}
