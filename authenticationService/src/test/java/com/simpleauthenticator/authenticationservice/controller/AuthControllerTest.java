package com.simpleauthenticator.authenticationservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.simpleauthenticator.authenticationservice.model.RoleName;
import com.simpleauthenticator.authenticationservice.model.User;
import com.simpleauthenticator.authenticationservice.payload.request.SignUpRequest;
import com.simpleauthenticator.authenticationservice.security.CustomUserDetailsService;
import com.simpleauthenticator.authenticationservice.security.JwtAuthenticationEntryPoint;
import com.simpleauthenticator.authenticationservice.security.JwtTokenProvider;
import com.simpleauthenticator.authenticationservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.servlet.Filter;
import java.util.ArrayList;
import java.util.List;

import static com.simpleauthenticator.authenticationservice.model.RoleName.ROLE_USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = AuthController.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@EnableWebMvc
class AuthControllerTest {

    public static final String EMAIL = "test@test.com";
    public static final String NAME = "test";
    public static final String PASSWORD = "password";
    @MockBean
    private UserService userService;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private JwtTokenProvider tokenProvider;

    @InjectMocks
    AuthController authController;

    @Autowired
    protected WebApplicationContext wac;

    @MockBean
    private JwtAuthenticationEntryPoint jwt;

    protected MockMvc mockMvc;

    private List<RoleName> roleNameList;


    @BeforeEach
    void setup() {
        Filter[] springSecurityFilterChain = new Filter[]{};
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).addFilters(springSecurityFilterChain).build();
        roleNameList= new ArrayList<>();
        roleNameList.add(ROLE_USER);
    }

    @Test
    @DisplayName("Register new user when user signs up with unique email address")
    void testSignUp() throws Exception {
        when(userService.existsByEmail(any())).thenReturn(false);
        when(userService.registerUser(any(SignUpRequest.class))).thenReturn(User.builder().build());

        SignUpRequest signUpRequest = SignUpRequest.builder()
                .email(EMAIL)
                .name(NAME)
                .password(PASSWORD)
                .roleNames(roleNameList)
                .build();

        mockMvc.perform(post("/api/auth/signup")
                .contentType("application/json")
                .content(asJsonString(signUpRequest)))
                .andExpect(status().isOk());

        verify(userService).registerUser(any());
    }

    public static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}