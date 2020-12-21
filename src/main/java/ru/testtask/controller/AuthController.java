package ru.testtask.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.testtask.config.jwt.JwtProvider;
import ru.testtask.dto.AuthUserDTO;
import ru.testtask.model.User;
import ru.testtask.service.UserService;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserService userService;


    @PostMapping("/auth")
    public ResponseEntity<String> auth(@RequestBody AuthUserDTO authUserDTO) {
        User user;
        try {
            user = userService.getAuthorizedUser(authUserDTO.getUsername(), authUserDTO.getPassword());
        }
        catch (UsernameNotFoundException e){
            return new ResponseEntity<>("User with this username does not exist", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(jwtProvider.generateToken(user.getUsername()), HttpStatus.OK);
    }
}
