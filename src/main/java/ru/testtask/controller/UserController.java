package ru.testtask.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.testtask.dto.UserDTO;
import ru.testtask.dto.ViewUserDTO;
import ru.testtask.exception.NameAlreadyExistsException;
import ru.testtask.model.User;
import ru.testtask.service.UserService;

import java.util.ArrayList;
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
            user.setUsername(clientUser.getUsername());
            user.setPassword(clientUser.getPassword());
            userService.updateUser(user);
            return new ResponseEntity<>(user, HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") String id) {
        Optional<User> optionalUser = userService.getUserById(id);

        if (optionalUser.isEmpty())
            return new ResponseEntity<>(String.format("User with ID %s does not found", id), HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(modelMapper.map(optionalUser.get(), ViewUserDTO.class), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/name/{name}")
    public ResponseEntity<?> getUserByUsername(@PathVariable ("name") String name) {
        User user = userService.getUserByUsername(name);

        if (user == null)
            return new ResponseEntity<>(String.format("User with name %s does not found", name), HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(modelMapper.map(user, ViewUserDTO.class), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping()
    public ResponseEntity<UserDTO> postUser(@RequestBody UserDTO clientUser) {
        if (clientUser.getUsername() == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        User user = modelMapper.map(clientUser, User.class);

        try {
            return new ResponseEntity<UserDTO>(modelMapper.map(userService.createUser(user), UserDTO.class), HttpStatus.CREATED);
        }
        catch(NameAlreadyExistsException e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping()
    public ResponseEntity<List<ViewUserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<ViewUserDTO> viewUserDTOS = new ArrayList<>();

        for (User user : users) {
            viewUserDTOS.add(modelMapper.map(user, ViewUserDTO.class));
        }

        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(viewUserDTOS, HttpStatus.OK);
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
