package com.simpleauthenticator.authenticationservice.service;

import com.simpleauthenticator.authenticationservice.exception.AppException;
import com.simpleauthenticator.authenticationservice.model.Role;
import com.simpleauthenticator.authenticationservice.model.RoleName;
import com.simpleauthenticator.authenticationservice.model.User;
import com.simpleauthenticator.authenticationservice.payload.request.SignUpRequest;
import com.simpleauthenticator.authenticationservice.repository.RoleRepository;
import com.simpleauthenticator.authenticationservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User registerUser(SignUpRequest signUpRequest) {

        User user = User.builder()
                .name(signUpRequest.getName())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .roles(getUserRoles(signUpRequest.getRoleNames()))
                .build();

        return userRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private Set<Role> getUserRoles(List<RoleName> roleNames) {
        return roleNames.stream().map(item ->
                roleRepository.findByName(item)
                        .orElseThrow(() -> new AppException("User Role not set."))
        ).collect(Collectors.toSet());
    }
}
