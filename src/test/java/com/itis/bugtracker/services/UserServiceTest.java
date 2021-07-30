package com.itis.bugtracker.services;


import com.itis.bugtracker.BugTrackerLibApi.services.UserService;
import com.itis.bugtracker.BugTrackerLibImpl.models.data.Task;
import com.itis.bugtracker.BugTrackerLibImpl.models.data.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    public void changeRoleTest() {
        User developer = User.builder().role(User.Role.DEVELOPER).build();
        Task task = Task.builder().status(Task.Status.IN_PROGRESS).build();
        developer.getTakenTasks().add(task);
        try {
            userService.changeRole(developer, User.Role.MANAGER);
            Assert.fail();
        } catch (IllegalStateException e) {
            Assert.assertTrue(true);
        }
    }
}
