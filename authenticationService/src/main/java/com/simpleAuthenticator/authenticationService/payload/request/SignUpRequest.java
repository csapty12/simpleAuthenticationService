package com.simpleAuthenticator.authenticationService.payload.request;

import com.simpleAuthenticator.authenticationService.model.Role;
import com.simpleAuthenticator.authenticationService.model.RoleName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@ToString
public class SignUpRequest {
    @NotBlank
    @Size(min = 4, max = 40)
    private String name;

    @NotBlank
    @Size(max = 40)
    @Email
    private String email;

    @NotBlank
    @Size(min = 6, max = 20)
    private String password;

    private List<RoleName> roleNames;

}
