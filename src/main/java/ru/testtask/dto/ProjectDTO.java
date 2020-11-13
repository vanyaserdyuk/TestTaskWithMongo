package ru.testtask.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    private String id;
    private String name;
    private List<AttrDTO> attrs;
}
