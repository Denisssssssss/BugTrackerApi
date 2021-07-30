package com.itis.bugtracker.BugTrackerLibImpl.services;

import com.itis.bugtracker.BugTrackerLibApi.repositories.UserRepository;
import com.itis.bugtracker.BugTrackerLibApi.services.UserService;
import com.itis.bugtracker.BugTrackerLibImpl.models.data.Task;
import com.itis.bugtracker.BugTrackerLibImpl.models.data.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElse(null);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElse(null);
    }

    @Override
    public void changeRole(User user, User.Role role) {
        List<Task> tasks = user.getTakenTasks();
        if (!tasks.isEmpty()) {
            boolean isManager = role.equals(User.Role.MANAGER);
            boolean forbiddenForTestEngineer = tasks.stream()
                    .map(Task::getStatus).anyMatch(x -> (x.equals(Task.Status.DEV_TEST) ||
                    x.equals(Task.Status.IN_PROGRESS) ||
                    x.equals(Task.Status.REVIEW))) && role.equals(User.Role.TEST_ENGINEER);
            boolean forbiddenForDeveloper = tasks.stream()
                    .map(Task::getStatus).anyMatch(x -> x.equals(Task.Status.TESTING)) && role.equals(User.Role.DEVELOPER);
            if (isManager || forbiddenForDeveloper || forbiddenForTestEngineer) {
                throw new IllegalStateException("User has active tasks");
            }
        }
        user.setRole(role);
        userRepository.save(user);
    }
}
