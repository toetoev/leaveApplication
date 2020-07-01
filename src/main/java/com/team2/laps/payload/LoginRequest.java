package com.team2.laps.payload;

import javax.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank
    private String nameOrEmail;

    @NotBlank
    private String password;
}
