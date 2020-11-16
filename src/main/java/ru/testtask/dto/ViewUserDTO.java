package ru.testtask.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import ru.testtask.model.Role;

import java.util.Set;

@Data
@NoArgsConstructor
public class ViewUserDTO {
    private String id;
    private String username;
    private Set<Role> roles;
}
