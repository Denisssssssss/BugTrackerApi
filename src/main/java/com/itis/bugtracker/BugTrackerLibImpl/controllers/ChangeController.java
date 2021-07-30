package com.itis.bugtracker.BugTrackerLibImpl.controllers;

import com.itis.bugtracker.BugTrackerLibApi.services.ChangeService;
import com.itis.bugtracker.BugTrackerLibImpl.models.domain.body.ChangeBody;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.itis.bugtracker.BugTrackerLibImpl.models.domain.body.ChangeBody.from;

@RestController
@RequestMapping("/changelist")
public class ChangeController {

    private final ChangeService changeService;

    @Autowired
    public ChangeController(ChangeService changeService) {
        this.changeService = changeService;
    }

    @ApiOperation(
            value = "Get list of changes of tasks",
            notes = "Parameters: id - number of task, returns list of objects",
            response = ChangeBody.class
    )
    @ApiResponse(code = 200, message = "Returns list of changes for specified task")
    @GetMapping
    public ResponseEntity<List<ChangeBody>> getChangeList(@RequestParam(value = "id", required = false) Long id) {

        List<ChangeBody> changeList;
        if (id == null) {
            changeList = from(changeService.findAll());
        } else {
            changeList = from(changeService.findAllById(id));
        }
        return ResponseEntity.ok(changeList);
    }
}
