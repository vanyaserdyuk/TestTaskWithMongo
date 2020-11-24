package ru.testtask.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Document(collection = "files")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class FileData {
    @Id
    private String id;
    @Indexed(unique = true)
    private String filename;
    private long size;
    private String type;
    private String originalFilename;
    private String directory;
}
