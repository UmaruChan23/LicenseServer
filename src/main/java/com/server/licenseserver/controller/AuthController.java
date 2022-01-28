package com.server.licenseserver.controller;

import com.server.licenseserver.entity.User;
import com.server.licenseserver.exception.UserAlreadyExistsException;
import com.server.licenseserver.exception.UserNotFoundException;
import com.server.licenseserver.model.AuthRequest;
import com.server.licenseserver.model.AuthResponse;
import com.server.licenseserver.model.RegistrationRequest;
import com.server.licenseserver.security.jwt.JwtProvider;
import com.server.licenseserver.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class AuthController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtProvider jwtProvider;

    @PostMapping("/register")
    public String registerUser(@RequestBody @Valid RegistrationRequest registrationRequest)
            throws UserAlreadyExistsException {
        User user = new User();
        user.setPassword(registrationRequest.getPassword());
        user.setLogin(registrationRequest.getLogin());
        userService.saveUser(user);
        return "OK";
    }

    @PostMapping("/auth")
    public AuthResponse auth(@RequestBody AuthRequest request) throws UserNotFoundException {
        User user = userService.findByLoginAndPassword(request.getLogin(), request.getPassword());
        String token = jwtProvider.generateToken(user.getLogin(), request.getDeviceId());
        return new AuthResponse(token);
    }
}
