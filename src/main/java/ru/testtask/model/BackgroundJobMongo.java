package ru.testtask.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Document(collection = "jobs")
@Data
public class BackgroundJobMongo {
    @Id
    private String id;
    private String name;
    private int progress;
    private BackgroundJobStatus jobStatus;
}
