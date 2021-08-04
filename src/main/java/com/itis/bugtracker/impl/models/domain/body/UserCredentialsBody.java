package com.itis.bugtracker.impl.models.domain.body;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserCredentialsBody {
    private final String username;
    private final String password;
}
