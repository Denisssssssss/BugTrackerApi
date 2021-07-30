package com.itis.bugtracker.BugTrackerLibApi.repositories;

import com.itis.bugtracker.BugTrackerLibImpl.models.data.Change;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChangeRepository extends JpaRepository<Change, Long> {

    List<Change> findAllById(Long id);
}