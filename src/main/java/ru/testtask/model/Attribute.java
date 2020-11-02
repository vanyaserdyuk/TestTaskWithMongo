package ru.testtask.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;


@Builder
@Data
@AllArgsConstructor
public class Attribute {
    private String id;
    private String name;

}
