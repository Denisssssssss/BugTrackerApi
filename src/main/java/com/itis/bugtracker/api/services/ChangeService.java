package com.itis.bugtracker.api.services;

import com.itis.bugtracker.impl.models.data.Change;

import java.util.List;

public interface ChangeService {

    Change save(Change change);

    List<Change> findAll();

    List<Change> findAllById(Long id);
}
