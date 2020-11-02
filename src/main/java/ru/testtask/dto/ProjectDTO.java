package ru.testtask.dto;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectDTO {
    private String id;
    private String name;
    private List<AttrDTO> attributes;
}
