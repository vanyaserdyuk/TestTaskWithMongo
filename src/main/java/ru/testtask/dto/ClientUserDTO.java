package ru.testtask.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.testtask.model.Role;

import java.util.Set;

@Data
@NoArgsConstructor
public class ClientUserDTO {
    private String name;
    @JsonDeserialize(as = Set.class)
    private Set<Role> roles;
}
