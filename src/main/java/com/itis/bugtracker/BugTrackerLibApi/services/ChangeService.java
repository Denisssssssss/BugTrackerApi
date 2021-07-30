package com.itis.bugtracker.BugTrackerLibApi.services;

import com.itis.bugtracker.BugTrackerLibImpl.models.data.Change;

import java.util.List;

public interface ChangeService {

    Change save(Change change);

    List<Change> findAll();

    List<Change> findAllById(Long id);
}
