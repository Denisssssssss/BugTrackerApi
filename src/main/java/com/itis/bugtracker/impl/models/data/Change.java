package com.itis.bugtracker.impl.models.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Change {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Task task;

    public enum Target {
        STATUS_EXECUTOR, BLOCK_LIST, BLOCKED_BY_LIST, CREATION, MULTI
    }

    @Enumerated(EnumType.STRING)
    private Target target;

    private Date date;

    public Change(Task task, Target target, Date date) {
        this.task = task;
        this.target = target;
        this.date = date;
    }
}
