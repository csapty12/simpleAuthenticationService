package com.simpleauthenticator.authenticationservice.service;

import com.simpleauthenticator.authenticationservice.model.User;
import com.simpleauthenticator.authenticationservice.payload.request.SignUpRequest;


public interface UserService {
    User registerUser(SignUpRequest signUpRequest);

    boolean existsByEmail(String email);
}
