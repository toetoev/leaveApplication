package com.team2.laps.payload;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "Name or email cannot be blank")
    private String nameOrEmail;

    @NotBlank(message = "Password cannot be empty")
    private String password;
}
