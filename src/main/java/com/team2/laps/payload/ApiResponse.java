package com.team2.laps.payload;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ApiResponse {
    @JsonIgnore
    private Boolean success;
    @JsonIgnore
    private String message;
    private Object data;

    public ApiResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ApiResponse(Object data) {
        this.data = data;
    }
}
