package ru.testtask.model;

import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class Attribute {
    private String id;
    private String name;

}
