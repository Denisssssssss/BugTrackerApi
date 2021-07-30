package com.itis.bugtracker.BugTrackerLibImpl.models.domain.body;

import com.itis.bugtracker.BugTrackerLibImpl.models.data.Task;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Data
public class TaskBody {

    private final String status;
    private final String type;
    private final String priority;
    private final String title;
    private final String description;
    private final String author;
    private final String executor;
    private final List<TaskBody> blocks;
    private final Date created;
    private final Date modified;

    public static TaskBody from(Task task) {
        List<TaskBody> blocks = from(task.getBlocks());
        String executor = task.getExecutor() == null ? "empty" : task.getExecutor().getUsername();
        return new TaskBody(task.getStatus().name(),
                task.getType().name(),
                task.getPriority().name(),
                task.getTitle(),
                task.getDescription(),
                task.getAuthor().getUsername(),
                executor,
                blocks,
                task.getCreated(),
                task.getLastModified()
        );
    }

    public static List<TaskBody> from (List<Task> tasks) {
        return tasks.stream().map(TaskBody::from).collect(Collectors.toList());
    }
}
