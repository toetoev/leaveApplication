package com.team2.laps.payload;

import com.team2.laps.model.RoleName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtAuthenticationResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private RoleName roleName;

    public JwtAuthenticationResponse(String accessToken, RoleName roleName) {
        this.accessToken = accessToken;
        this.roleName = roleName;
    }
}
