package ru.testtask.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import ru.testtask.service.BackgroundJobService;

import javax.persistence.Id;

@Document(collection = "jobs")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BackgroundJob {
    @Id
    private String id;
    private String name;
    private int progress;
    private BackgroundJobStatus jobStatus;

    @Transient
    @Nullable
    private Runnable jobExecution;
}
