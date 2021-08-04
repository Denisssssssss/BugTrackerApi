package com.itis.bugtracker.impl.services;

import com.itis.bugtracker.api.repositories.ChangeRepository;
import com.itis.bugtracker.api.services.ChangeService;
import com.itis.bugtracker.impl.models.data.Change;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChangeServiceImpl implements ChangeService {

    private final ChangeRepository changeRepository;

    @Autowired
    public ChangeServiceImpl(ChangeRepository changeRepository) {
        this.changeRepository = changeRepository;
    }

    @Override
    public Change save(Change change) {
        return changeRepository.save(change);
    }

    @Override
    public List<Change> findAll() {
        return changeRepository.findAll();
    }

    @Override
    public List<Change> findAllById(Long id) {
        return changeRepository.findAllById(id);
    }
}
