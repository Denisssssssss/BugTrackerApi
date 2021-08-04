package com.itis.bugtracker.impl.models.domain.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;

@Getter
@Setter
public class UserDataDTO {
    @Pattern(regexp = "\\bMANAGER|TEAM_LEAD|DEVELOPER|TEST_ENGINEER\\b")
    private String role;
    private TaskDTO task;
}
