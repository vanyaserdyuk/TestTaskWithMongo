package ru.testtask.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectUser {
    @Id
    private String id;
    private String username;
    private String password;
}
