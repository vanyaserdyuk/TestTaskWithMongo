package ru.testtask.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.testtask.service.BackgroundJobService;

import javax.persistence.Id;

@Document(collection = "jobs")
@Data
@Builder
public class BackgroundJob {
    @Id
    private String id;
    private String name;
    private int progress;
    private BackgroundJobStatus jobStatus;

    @Transient
    private Runnable jobExecution;

    @Transient
    private boolean running;
}
