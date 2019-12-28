package com.simpleAuthenticator.authenticationService.payload.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class APIResponse {
    private Boolean success;
    private String message;
}
