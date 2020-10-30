package ru.testtask.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
public class AttrDTO {
    private String id;
    private String name;
}
