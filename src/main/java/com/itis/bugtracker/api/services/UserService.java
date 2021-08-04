package com.itis.bugtracker.api.services;

import com.itis.bugtracker.impl.models.data.User;

public interface UserService {

    User save(User user);

    User findById(Long id);

    User findByUsername(String username);

    void changeRole(User user, User.Role role);
}
