package com.itis.bugtracker.impl.models.domain.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
public class TaskInfoDTO {

    @Pattern(regexp = "\\bBUG|TASK\\b")
    private String type;

    @Pattern(regexp = "\\bLOW|MEDIUM|CRITICAL\\b")
    private String priority;

    private String title;

    private String description;

    private List<Long> blocks;
}
