package com.team2.laps.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JwtAuthenticationResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private String roleName;
    private String name;

    public JwtAuthenticationResponse(String accessToken, String roleName, String name) {
        this.accessToken = accessToken;
        this.roleName = roleName;
        this.name = name;
    }
}
