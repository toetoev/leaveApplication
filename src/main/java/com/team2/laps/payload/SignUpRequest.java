package com.team2.laps.payload;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.team2.laps.model.RoleName;
import com.team2.laps.validation.UniqueEmail;
import com.team2.laps.validation.UniqueName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignUpRequest {
    @NotBlank
    @Size(max = 15)
    @UniqueName
    private String name;

    @NotBlank
    @Size(max = 40)
    @Email
    @UniqueEmail
    private String email;

    @NotBlank
    @Size(min = 6, max = 20)
    private String password;

    private RoleName role;
}
