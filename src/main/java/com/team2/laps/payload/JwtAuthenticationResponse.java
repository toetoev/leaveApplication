package com.team2.laps.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtAuthenticationResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private String roleName;

    public JwtAuthenticationResponse(String accessToken, String roleName) {
        this.accessToken = accessToken;
        this.roleName = roleName;
    }
}
