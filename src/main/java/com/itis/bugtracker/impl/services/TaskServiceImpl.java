package com.itis.bugtracker.impl.services;

import com.itis.bugtracker.api.repositories.ChangeRepository;
import com.itis.bugtracker.api.repositories.TaskRepository;
import com.itis.bugtracker.api.repositories.UserRepository;
import com.itis.bugtracker.api.services.TaskService;
import com.itis.bugtracker.impl.models.data.Change;
import com.itis.bugtracker.impl.models.data.Task;
import com.itis.bugtracker.impl.models.data.User;
import com.itis.bugtracker.impl.models.domain.dto.TaskInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.Instant;
import java.util.Collections;
import java.util.List;


@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final ChangeRepository changeRepository;
    private final UserRepository userRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository,
                           ChangeRepository changeRepository,
                           UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.changeRepository = changeRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Task save(Task task) {

        task.modify();
        Change change = new Change(task, Change.Target.CREATION, java.util.Date.from(Instant.now()));
        task.getChangeList().add(change);
        taskRepository.save(task);
        changeRepository.save(change);
        return task;
    }

    @Override
    public Task update(Task task, Change.Target target) {

        task.modify();
        Change change = new Change(task, target, Date.from(Instant.now()));
        task.getChangeList().add(change);
        changeRepository.save(change);
        return taskRepository.save(task);
    }

    @Override
    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Task not found"));
        if (task.getExecutor() != null) {
            task.getExecutor().getTakenTasks().remove(task);
            userRepository.save(task.getExecutor());
        }
        for (Task t : task.getBlocks()) {
            t.getBlockedBy().remove(task);
            update(t, Change.Target.BLOCKED_BY_LIST);
        }
        for (Task t : task.getBlockedBy()) {
            t.getBlocks().remove(task);
            update(t, Change.Target.BLOCK_LIST);
        }
        taskRepository.deleteById(id);
    }

    @Override
    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Task not found"));
    }

    @Override
    public Task changeState(Task task, User user, Task.Status status) {
        User executor = task.getExecutor();

        if (executor == null && user == null) {
            return null;
        }

        if (status.equals(Task.Status.TODO)) {
            task.reset();
            task.setExecutor(user);
            updateExecutor(executor == null, user, executor, task);
            return update(task, Change.Target.STATUS_EXECUTOR);
        }
        if (status.equals(Task.Status.WONTFIX)) {
            task.skip();
            task.setExecutor(null);
            task.getBlocks().removeAll(task.getBlocks());
            for (Task t : task.getBlocks()) {
                t.getBlockedBy().remove(task);
                update(t, Change.Target.BLOCKED_BY_LIST);
            }
            updateExecutor(executor == null, user, executor, task);
            return update(task, Change.Target.STATUS_EXECUTOR);
        }
        if (validate(status, user) && (status.equals(task.nextStatus()) || status.equals(task.getStatus()))) {
            task.setStatus(status);
            task.setExecutor(user);
            if (task.getStatus().equals(Task.Status.DONE)) {
                User u = task.getExecutor();
                task.getExecutor().getTakenTasks().remove(task);
                userRepository.save(u);
                for (Task t : task.getBlocks()) {
                    t.getBlockedBy().remove(task);
                    update(t, Change.Target.BLOCKED_BY_LIST);
                }
            }
            updateExecutor(executor == null, user, executor, task);
            return update(task, Change.Target.STATUS_EXECUTOR);
        }

        return null;
    }

    private void updateExecutor(boolean execIsNull, User user, User executor, Task task) {

        if (execIsNull) {
            user.getTakenTasks().add(task);
        } else {
            user.getTakenTasks().add(task);
            executor.getTakenTasks().remove(task);
            userRepository.save(executor);
        }
        userRepository.save(user);
    }

    private boolean validate(Task.Status status, User executor) {

        //executor == null -> task has no executor
        if (executor == null) {
            return !status.equals(Task.Status.IN_PROGRESS);
        }

        User.Role role = executor.getRole();

        if (role.equals(User.Role.MANAGER)) {
            return false;
        }
        if (role.equals(User.Role.TEAM_LEAD)) {
            return true;
        }
        if ((status.equals(Task.Status.IN_PROGRESS)
                || status.equals(Task.Status.DEV_TEST)
                || status.equals(Task.Status.REVIEW))
                && role.equals(User.Role.TEST_ENGINEER)) {
            return false;
        }
        return !status.equals(Task.Status.TESTING) || !role.equals(User.Role.DEVELOPER);
    }

    @Override
    public List<Task> search(String title,
                             String description,
                             String type,
                             String status,
                             Long author,
                             Long executor,
                             Long number,
                             boolean random) {
        if (author == null) {
            author = 0L;
        }
        if (executor == null) {
            executor = 0L;
        }
        if (number == null) {
            number = 0L;
        }
        List<Task> list = taskRepository.search(title, description, type, status, author, executor, number);
        if (random) {
            Collections.shuffle(list);
        }
        return list;
    }

    @Override
    public Task addBlockedTasks(Task task, List<Long> taskIds) {
        for (Long taskId : taskIds) {
            Task taskToAdd = findById(taskId);
            taskToAdd.getBlockedBy().add(task);
            task.getBlocks().add(taskToAdd);
            update(taskToAdd, Change.Target.BLOCKED_BY_LIST);
        }
        return update(task, Change.Target.BLOCK_LIST);
    }

    @Override
    public Task changeInfo(Task task, TaskInfoDTO taskDTO) {
        Task.Priority priority;
        if (taskDTO.getPriority() == null) {
            priority = task.getPriority();
        } else {
            priority = Task.Priority.valueOf(taskDTO.getPriority());
        }
        Task.Type type;
        if (taskDTO.getType() == null) {
            type = task.getType();
        } else {
            type = Task.Type.valueOf(taskDTO.getType());
        }
        String description = task.getDescription();
        String title = taskDTO.getTitle();

        task.setType(type);
        task.setPriority(priority);
        if (description != null) {
            task.setDescription(description);
        }
        if (title != null) {
            task.setTitle(title);
        }

        return update(task, Change.Target.MULTI);
    }
}
