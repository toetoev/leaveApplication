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
    @NotBlank(message = "Name cannot be blank")
    @Size(max = 15)
    @UniqueName
    private String name;

    @NotBlank(message = "Email cannot be blank")
    @Size(max = 40)
    @Email
    @UniqueEmail
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 20, message = "Password should have 6-20 characters")
    private String password;

    private RoleName role;
}
