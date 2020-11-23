package ru.testtask.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.testtask.config.jwt.JwtProvider;
import ru.testtask.dto.AuthUserDTO;
import ru.testtask.model.User;
import ru.testtask.service.UserService;

@RestController
@RequestMapping("/api")
public class AuthController {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserService userService;


    @PostMapping("/auth")
    public String auth(@RequestBody AuthUserDTO authUserDTO) {
        User user = userService.getAuthorizedUser(authUserDTO.getUsername(), authUserDTO.getPassword());
        return jwtProvider.generateToken(user.getUsername());
    }
}
