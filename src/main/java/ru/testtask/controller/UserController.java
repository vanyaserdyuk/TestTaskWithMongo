package ru.testtask.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.testtask.dto.ClientUserDTO;
import ru.testtask.exception.WrongMethodUseException;
import ru.testtask.model.User;
import ru.testtask.service.UserService;

import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserService userService;


    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/user/{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") String id, @RequestBody ClientUserDTO clientUser) {
        Optional<User> optionalUser = userService.getUserById(id);
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            user.setRoles(clientUser.getRoles());
            userService.updateUser(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
