package com.itis.bugtracker.BugTrackerLibImpl.models.data;

import lombok.*;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "account")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public enum Role {
        MANAGER, TEAM_LEAD, DEVELOPER, TEST_ENGINEER
    }

    @Enumerated(value = EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "author")
    @Builder.Default
    private List<Task> createdTasks = new LinkedList<>();

    @OneToMany(mappedBy = "executor")
    @Builder.Default
    private List<Task> takenTasks = new LinkedList<>();

    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String hashPassword;
}