package ru.testtask.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Document(collection = "files")
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@CompoundIndexes({
        @CompoundIndex(name = "directory_originalFilename", def = "{'directory' : 1, 'originalFilename' : 1}"
                , sparse = true, unique = true)
})
public class FileData {
    @Id
    private String id;
    private String filename;
    private long size;
    private String type;
    private String originalFilename;
    private String directory;
}
