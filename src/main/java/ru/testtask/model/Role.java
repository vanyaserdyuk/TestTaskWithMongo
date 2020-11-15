package ru.testtask.model;


import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

@ToString
public enum Role implements GrantedAuthority {
    USER, ADMIN, MODERATOR;

    @Override
    public String getAuthority() {
        return name();
    }
}
