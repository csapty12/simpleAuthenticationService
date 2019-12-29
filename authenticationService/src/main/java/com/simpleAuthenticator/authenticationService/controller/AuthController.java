package com.simpleAuthenticator.authenticationService.controller;

import com.simpleAuthenticator.authenticationService.exception.AppException;
import com.simpleAuthenticator.authenticationService.model.Role;
import com.simpleAuthenticator.authenticationService.model.User;
import com.simpleAuthenticator.authenticationService.payload.request.LoginRequest;
import com.simpleAuthenticator.authenticationService.payload.request.SignUpRequest;
import com.simpleAuthenticator.authenticationService.payload.response.APIResponse;
import com.simpleAuthenticator.authenticationService.payload.response.JwtAuthenticationResponse;
import com.simpleAuthenticator.authenticationService.repository.RoleRepository;
import com.simpleAuthenticator.authenticationService.repository.UserRepository;
import com.simpleAuthenticator.authenticationService.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashSet;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtTokenProvider tokenProvider;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return new ResponseEntity(new APIResponse(false, "Email Address already in use!"),
                    HttpStatus.BAD_REQUEST);
        }

        User user = new User(signUpRequest.getName(),
                signUpRequest.getEmail(), signUpRequest.getPassword());

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Set<Role> roles = new HashSet<>();
        signUpRequest.getRoleNames().forEach(item -> {
            Role userRole = roleRepository.findByName(item)
                    .orElseThrow(() -> new AppException("User Role not set."));
            roles.add(userRole);
        });
        user.setRoles(roles);
        User result = userRepository.save(user);
        return ResponseEntity.ok().body(new APIResponse(true, "User registered successfully"));
    }
}
