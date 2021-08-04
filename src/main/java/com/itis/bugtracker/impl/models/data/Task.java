package com.itis.bugtracker.impl.models.data;

import lombok.*;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public enum Status {
        TODO, IN_PROGRESS, REVIEW, DEV_TEST, TESTING, DONE, WONTFIX;

        public static final Status[] values = values();

        public Status next() {
            return values[(ordinal() + 1) % values.length];
        }

        public Status previous() {
            return values[(ordinal() - 1 + values.length) % values.length];
        }
    }

    public enum Type {
        BUG, TASK
    }

    public enum Priority {
        LOW, MEDIUM, CRITICAL
    }

    private String title;
    private String description;

    @Builder.Default
    @Enumerated(value = EnumType.STRING)
    private Status status = Status.TODO;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @Enumerated(value = EnumType.STRING)
    private Priority priority;

    @ManyToMany
    @JoinTable(name = "blocks",
            joinColumns = @JoinColumn(name = "task_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "blocked_task_id", referencedColumnName = "id")
    )
    private List<Task> blocks;

    @ManyToMany(mappedBy = "blocks")
    private List<Task> blockedBy;

    @ManyToOne
    private User author;

    @ManyToOne
    private User executor;

    @OneToMany(cascade = CascadeType.ALL)
    private List<Change> changeList;

    private Date created;
    private Date lastModified;

    public Status nextStatus() {
        return this.status.next();
    }

    public Status prevStatus() {
        return this.status.previous();
    }

    public void reset() {
        this.setStatus(Status.TODO);
        this.setLastModified(Date.from(Instant.now()));
    }

    public void skip() {
        this.setStatus(Status.WONTFIX);
        this.setLastModified(Date.from(Instant.now()));
    }

    public void modify() {
        this.setLastModified(Date.from(Instant.now()));
    }
}