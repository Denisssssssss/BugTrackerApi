package com.itis.bugtracker.api.services;

import com.itis.bugtracker.impl.models.data.Change;
import com.itis.bugtracker.impl.models.data.Task;
import com.itis.bugtracker.impl.models.data.User;
import com.itis.bugtracker.impl.models.domain.dto.TaskInfoDTO;

import java.util.List;

public interface TaskService {

    Task save(Task task);

    Task update(Task task, Change.Target target);

    List<Task> findAll();

    void deleteById(Long id);

    Task findById(Long id);

    Task changeState(Task task, User user, Task.Status status);

    List<Task> search(String title,
                      String description,
                      String type,
                      String status,
                      Long author,
                      Long executor,
                      Long number,
                      boolean random);

    Task addBlockedTasks(Task task, List<Long> taskIds);

    Task changeInfo(Task task, TaskInfoDTO taskInfoDTO);
}
