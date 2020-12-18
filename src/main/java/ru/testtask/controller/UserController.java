package ru.testtask.controller;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import ru.testtask.dto.CreateUpdateUserDTO;
import ru.testtask.dto.UserDTO;
import ru.testtask.exception.NameAlreadyExistsException;
import ru.testtask.model.User;
import ru.testtask.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;

    private final ModelMapper modelMapper;

    public UserController(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }


    @PutMapping("{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable("id") String id, @RequestBody CreateUpdateUserDTO clientUser) {

        Optional<User> optionalUser = userService.getUserById(id);
        if (optionalUser.isPresent()){
            User user = optionalUser.get();
            user.setRoles(clientUser.getRoles());
            user.setUsername(clientUser.getUsername());
            user.setPassword(clientUser.getPassword());
            userService.updateUser(user);
            return new ResponseEntity<>(modelMapper.map(user, UserDTO.class), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable("id") String id) {
        Optional<User> optionalUser = userService.getUserById(id);

        if (optionalUser.isEmpty())
            return new ResponseEntity<>(String.format("User with ID %s does not found", id), HttpStatus.NOT_FOUND);

        return new ResponseEntity<>(modelMapper.map(optionalUser.get(), UserDTO.class), HttpStatus.OK);
    }


    @GetMapping("/name/{name}")
    public ResponseEntity<?> getUserByUsername(@PathVariable ("name") String name) {
        User user;
        try {
            user = userService.getUserByUsername(name);
        }

        catch (UsernameNotFoundException e){
            return new ResponseEntity<>(String.format("User with name %s does not found", name), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(modelMapper.map(user, UserDTO.class), HttpStatus.OK);
    }


    @PostMapping()
    public ResponseEntity<UserDTO> postUser(@RequestBody CreateUpdateUserDTO clientUser) {
        if (clientUser.getUsername() == null){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        User user = modelMapper.map(clientUser, User.class);

        try {
            return new ResponseEntity<>(modelMapper.map(userService.createUser(user), UserDTO.class), HttpStatus.CREATED);
        }
        catch(NameAlreadyExistsException e){
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

    }


    @GetMapping()
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userService.getAllUsers();

        List<UserDTO> userDTOS = users.stream().map(user -> modelMapper.map(user, UserDTO.class)).collect(Collectors.toList());

        return new ResponseEntity<>(userDTOS, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") String id) {

        if (userService.getUserById(id).isEmpty()) {
            return new ResponseEntity<>(String.format("User with ID %s does not found", id), HttpStatus.NOT_FOUND);
        }

        userService.deleteUser(id);

        return new ResponseEntity<>(String.format("User with ID %s removed successfully", id), HttpStatus.NO_CONTENT);
    }
}
