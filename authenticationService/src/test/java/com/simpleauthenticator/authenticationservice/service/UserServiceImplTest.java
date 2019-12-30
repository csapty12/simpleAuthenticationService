package com.simpleauthenticator.authenticationservice.service;

import com.simpleauthenticator.authenticationservice.exception.AppException;
import com.simpleauthenticator.authenticationservice.model.Role;
import com.simpleauthenticator.authenticationservice.model.RoleName;
import com.simpleauthenticator.authenticationservice.model.User;
import com.simpleauthenticator.authenticationservice.payload.request.SignUpRequest;
import com.simpleauthenticator.authenticationservice.repository.RoleRepository;
import com.simpleauthenticator.authenticationservice.repository.UserRepository;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static com.simpleauthenticator.authenticationservice.model.RoleName.ROLE_USER;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    public static final String EMAIL_ADDRESS = "test@test.com";
    public static final String NAME = "bob";
    public static final String PASSWORD = "test";
    private static final String ROLE_INVALID = "ROLE_INVALID";

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    @DisplayName("When registering user, convert signup request to user object and save")
    void registerUser() {
        when(roleRepository.findByName(any())).thenReturn(Optional.of(new Role(ROLE_USER)));
        when(passwordEncoder.encode(any())).thenReturn("test");
        Set<Role> roles = getMockUserRoles();

        User expected = User.builder()
                .name(NAME)
                .email(EMAIL_ADDRESS)
                .password(PASSWORD)
                .roles(roles)
                .build();

        when(userRepository.save(any())).thenReturn(expected);

        SignUpRequest signUpRequest = SignUpRequest.builder()
                .email(EMAIL_ADDRESS)
                .name(NAME)
                .password(PASSWORD)
                .roleNames(new ArrayList<>(asList(ROLE_USER)))
                .build();
        User actual = userService.registerUser(signUpRequest);

        verify(userRepository).save(any());
        assertThat(actual.getEmail(), Is.is(expected.getEmail()));
        assertThat(actual.getRoles(), Is.is(expected.getRoles()));
    }

    @Test
    @DisplayName("When user role does not exist, throw exception")
    void rolesDoNotExist(){
        when(passwordEncoder.encode(any())).thenReturn(PASSWORD);
        SignUpRequest signUpRequest = SignUpRequest.builder()
                .email(EMAIL_ADDRESS)
                .name(NAME)
                .password(PASSWORD)
                .roleNames(new ArrayList<>(asList(RoleName.ROLE_USER)))
                .build();

        assertThrows(AppException.class, ()-> userService.registerUser(signUpRequest));

    }

    private Set<Role> getMockUserRoles() {
        Set<Role> roles = new HashSet<>();
        Role role = new Role(ROLE_USER);
        role.setId(1L);
        roles.add(role);
        return roles;
    }

}