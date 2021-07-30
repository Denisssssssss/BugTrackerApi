package com.itis.bugtracker.BugTrackerLibApi.services;

import com.itis.bugtracker.BugTrackerLibImpl.models.data.User;

public interface UserService {

    User save(User user);

    User findById(Long id);

    User findByUsername(String username);

    void changeRole(User user, User.Role role);
}
