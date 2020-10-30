package ru.testtask.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AttrDTO {
    private String id;
    private String name;
}
