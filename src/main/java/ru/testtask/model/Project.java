package ru.testtask.model;


import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.core.mapping.Document;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "projects")
@NoArgsConstructor
@Builder
@AllArgsConstructor
@Data
public class Project {

    @Id
    private String id;
    private String name;
    private String ownerId;

    @NotNull
    private List<Attribute> attributes = new ArrayList<>();

    @NotNull
    private List<Geometry> geometries = new ArrayList<>();

    public void addGeometry(Geometry geometry){
        this.geometries.add(geometry);
    }
    public void addAttribute(Attribute attribute){
        this.attributes.add(attribute);
    }
}
