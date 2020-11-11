package ru.testtask.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.testtask.converter.DTOConverterConfig;
import ru.testtask.dto.CreateProjectDTO;
import ru.testtask.dto.ProjectDTO;
import ru.testtask.dto.UserDTO;
import ru.testtask.exception.NameAlreadyExistsException;
import ru.testtask.model.Project;
import ru.testtask.model.User;
import ru.testtask.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("{id}")
    public ResponseEntity<User> updateUser(@PathVariable("id") String id, @RequestBody UserDTO clientUser) {

        Optional<User> optionalUser = userService.getUserById(id);
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            user.setRoles(clientUser.getRoles());
            userService.updateUser(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectById(@PathVariable("id") String id) {
        Optional<User> optionalUser = userService.getUserById(id);

        if (optionalUser.isEmpty())
            return new ResponseEntity<>(String.format("User with ID %s does not found", id), HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(optionalUser.get(), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/name/{name}")
    public ResponseEntity<?> getProjectByName(@PathVariable ("name") String name) {
        User user = userService.getUserByUsername(name);

        if (user == null)
            return new ResponseEntity<>(String.format("Project with name %s does not found", name), HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping()
    public ResponseEntity<UserDTO> postProject(@RequestBody UserDTO clientUser) {
        if (clientUser.getName() == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        User user = modelMapper.map(clientUser, User.class);

        try {
            userService.createUser(user);
            return new ResponseEntity<UserDTO>(modelMapper.map(user, UserDTO.class), HttpStatus.CREATED);
        }
        catch(NameAlreadyExistsException e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();

        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") String id) {

        if (userService.getUserById(id).isEmpty()) {
            return new ResponseEntity<>(String.format("User with ID %s does not found", id), HttpStatus.NOT_FOUND);
        }

        userService.deleteUser(id);

        return new ResponseEntity<>(String.format("User with ID %s removed successfully", id), HttpStatus.NO_CONTENT);
    }
}
