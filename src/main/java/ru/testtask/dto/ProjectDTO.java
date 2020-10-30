package ru.testtask.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
@Builder
public class ProjectDTO {
    @NonNull
    private String id;
    @NonNull
    private String name;
    private List<AttrDTO> attrs;
}
