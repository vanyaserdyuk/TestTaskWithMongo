package ru.testtask.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "projects")
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Project {

    @Id
    @Getter
    private String id;

    @Setter
    @Getter
    private String name;

    @Getter
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
