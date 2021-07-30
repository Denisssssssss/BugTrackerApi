package com.itis.bugtracker.BugTrackerLibImpl.controllers;

import com.itis.bugtracker.BugTrackerLibApi.security.token.TokenProvider;
import com.itis.bugtracker.BugTrackerLibApi.services.TaskService;
import com.itis.bugtracker.BugTrackerLibApi.services.UserService;
import com.itis.bugtracker.BugTrackerLibImpl.models.data.Change;
import com.itis.bugtracker.BugTrackerLibImpl.models.data.Task;
import com.itis.bugtracker.BugTrackerLibImpl.models.data.User;
import com.itis.bugtracker.BugTrackerLibImpl.models.domain.body.TaskBody;
import com.itis.bugtracker.BugTrackerLibImpl.models.domain.dto.TaskDTO;
import com.itis.bugtracker.BugTrackerLibImpl.models.domain.dto.TaskInfoDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.sql.Date;
import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static com.itis.bugtracker.BugTrackerLibImpl.models.domain.body.TaskBody.from;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TokenProvider tokenProvider;
    private final TaskService taskService;
    private final UserService userService;

    @Autowired
    public TaskController(TokenProvider tokenProvider,
                          UserService userService,
                          TaskService taskService) {
        this.tokenProvider = tokenProvider;
        this.userService = userService;
        this.taskService = taskService;
    }

    @ApiOperation(value = "Get all tasks",
            notes = "Param r - for random sort. " +
                    "By default, tasks are sorted by last modification time, returns list of objects",
            response = TaskBody.class)
    @ApiResponse(code = 200, message = "Returns list of tasks")
    @GetMapping
    public ResponseEntity<List<TaskBody>> getTasks(@RequestParam("r") Boolean random) {
        List<Task> tasks = taskService.findAll();
        if (random) {
            Collections.shuffle(tasks);
        }
        return ResponseEntity.ok(from(tasks));
    }

    @ApiOperation(value = "Search for tasks",
            notes = "Params: n - task number, " +
                    "h - task title, " +
                    "d - task description, " +
                    "t - type, " +
                    "s - status, " +
                    "author - author name, " +
                    "exec - executor name, " +
                    "r - for random sort." +
                    "Returns list of objects",
            response = TaskBody.class)
    @ApiResponse(code = 200, message = "Returns result of search")
    @GetMapping("/search")
    public ResponseEntity<List<TaskBody>> search(@RequestParam(value = "n", required = false) Long number,
                                                 @RequestParam(value = "h", required = false) String header,
                                                 @RequestParam(value = "d", required = false) String description,
                                                 @RequestParam(value = "t", required = false) String type,
                                                 @RequestParam(value = "s", required = false) String status,
                                                 @RequestParam(value = "author", required = false) String author,
                                                 @RequestParam(value = "exec", required = false) String exec,
                                                 @RequestParam(value = "r", required = false, defaultValue = "false") String rand) {
        boolean random = Boolean.parseBoolean(rand);
        Long authorId = null;
        Long execId = null;
        if (author != null) {
            authorId = userService.findByUsername(author).getId();
        }
        if (exec != null) {
            execId = userService.findByUsername(exec).getId();
        }

        return ResponseEntity.ok(from(taskService
                .search(header, description, type, status, authorId, execId, number, random))
        );
    }

    @ApiOperation(value = "Add new task",
            response = TaskBody.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Task successfully added"),
            @ApiResponse(code = 400, message = "Invalid format of input data")
    })
    @PostMapping("/add")
    public ResponseEntity<TaskBody> addTask(@RequestBody @Valid TaskDTO taskDTO,
                                            HttpServletRequest request,
                                            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        String token = request.getHeader("Access");
        Long authorId = tokenProvider.getUserIdFromToken(token);
        User author = userService.findById(authorId);
        Long executorId = taskDTO.getExecutorId();
        List<Task> blocks = new LinkedList<>();
        if (taskDTO.getBlocks() != null) {
            for (Long id : taskDTO.getBlocks()) {
                blocks.add(taskService.findById(id));
            }
        }

        if (taskDTO.getStatus().equals(Task.Status.IN_PROGRESS.name()) && taskDTO.getExecutorId() == null) {
            return ResponseEntity.badRequest().build();
        }

        Task task = Task.builder()
                .author(author)
                .created(Date.from(Instant.now()))
                .title(taskDTO.getTitle())
                .description(taskDTO.getDescription())
                .executor(executorId == null ? null : userService.findById(executorId))
                .priority(taskDTO.getPriority() == null ? Task.Priority.LOW : Task.Priority.valueOf(taskDTO.getPriority()))
                .status(Task.Status.valueOf(taskDTO.getStatus()))
                .type(Task.Type.valueOf(taskDTO.getType()))
                .blocks(blocks)
                .changeList(new LinkedList<>())
                .build();

        taskService.save(task);
        author.getCreatedTasks().add(task);
        userService.save(author);

        for (Task t : blocks) {
            t.getBlockedBy().add(task);
            taskService.update(t, Change.Target.BLOCKED_BY_LIST);
        }

        return ResponseEntity.ok(from(task));
    }

    @ApiOperation(value = "Refactor list of blocked tasks",
            notes = "Param id - id of the task",
            response = TaskBody.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully refactored"),
            @ApiResponse(code = 400, message = "Invalid format of input data")
    })
    @PutMapping("/refactor/blocks/{id}")
    public ResponseEntity<TaskBody> addBlockedTasks(@RequestBody @Valid TaskInfoDTO taskInfoDTO,
                                                    @PathVariable("id") Long id,
                                                    BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        Task task = taskService.findById(id);

        return ResponseEntity.ok(TaskBody.from(taskService
                .addBlockedTasks(task, taskInfoDTO.getBlocks()))
        );
    }

    @ApiOperation(value = "Refactor info of the task (type, priority, title, description)",
            notes = "Param id - id of the task",
            response = TaskBody.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully refactored"),
            @ApiResponse(code = 400, message = "Invalid format of input data")
    })
    @PutMapping("/refactor/info/{id}")
    public ResponseEntity<TaskBody> refactorTask(@RequestBody @Valid TaskInfoDTO taskDTO,
                                                 @PathVariable("id") Long id,
                                                 BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().build();
        }

        Task task = taskService.findById(id);

        return ResponseEntity.ok(TaskBody.from(taskService
                .changeInfo(task, taskDTO))
        );
    }

    @ApiOperation(value = "Refactor status and executor of the task",
            notes = "Param id - id of the task",
            response = TaskBody.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully refactored"),
            @ApiResponse(code = 400, message = "Conflict with costraints")
    })
    @PutMapping("/refactor/state")
    public ResponseEntity<TaskBody> setState(@RequestParam(value = "task") Long taskId,
                                             @RequestParam(value = "exec", required = false)
                                                     Long executorId,
                                             @RequestParam(value = "status", required = false)
                                                     String s) {

        Task task = taskService.findById(taskId);
        if (!task.getBlockedBy().isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        User user;
        if (executorId == null) {
            user = task.getExecutor();
        } else {
            user = userService.findById(executorId);
        }
        Task.Status status;
        if (s == null) {
            status = task.getStatus();
        } else {
            status = Task.Status.valueOf(s);
        }
        Task t = taskService.changeState(task, user, status);

        if (t == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(TaskBody.from(t));
    }

    @ApiOperation(value = "Delete task buy its number",
            notes = "Param id - number of the task")
    @ApiResponse(code = 200, message = "Successfully deleted")
    @DeleteMapping("/delete/{id}")
    public void deleteTask(@PathVariable("id") Long id) {
        taskService.deleteById(id);
    }
}