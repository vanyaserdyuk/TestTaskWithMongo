package ru.testtask.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.testtask.model.Role;

import java.util.Set;

@Data
@NoArgsConstructor
public class CreateUpdateUserDTO {
    private String id;
    private String username;
    private String password;
    @JsonDeserialize(as = Set.class)
    private Set<Role> roles;
}
