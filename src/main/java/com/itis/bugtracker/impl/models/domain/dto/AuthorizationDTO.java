package com.itis.bugtracker.impl.models.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthorizationDTO {

    @JsonProperty(required = true)
    private String username;

    @JsonProperty(required = true)
    private String password;
}
