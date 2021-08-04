package com.itis.bugtracker.impl.models.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;
import java.util.List;

@Getter
@Setter
public class TaskDTO {

    @JsonProperty(required = true)
    @Pattern(regexp = "\\bBUG|TASK\\b")
    private String type;

    @Pattern(regexp = "\\bLOW|MEDIUM|CRITICAL\\b")
    private String priority;

    @JsonProperty(required = true)
    @Pattern(regexp = "\\bTODO|IN_PROGRESS|REVIEW|DEV_TEST|TESTING|DONE|WONTFIX\\b")
    private String status;

    @JsonProperty(required = true)
    private String title;

    private String description;

    private List<Long> blocks;

    private Long executorId;
}
