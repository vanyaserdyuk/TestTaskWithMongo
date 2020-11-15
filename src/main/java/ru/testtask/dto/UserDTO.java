package ru.testtask.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.testtask.model.Role;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Set;

@Data
@NoArgsConstructor
public class UserDTO {
    private String username;
    private String password;
    @JsonDeserialize(as = Set.class)
    private Set<Role> roles;
}
