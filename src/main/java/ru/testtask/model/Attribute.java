package ru.testtask.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;


@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attribute {
    private String id;
    private String name;

}
