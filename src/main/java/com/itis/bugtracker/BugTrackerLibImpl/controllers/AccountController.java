package com.itis.bugtracker.BugTrackerLibImpl.controllers;

import com.itis.bugtracker.BugTrackerLibApi.services.UserService;
import com.itis.bugtracker.BugTrackerLibImpl.models.data.User;
import com.itis.bugtracker.BugTrackerLibImpl.models.domain.body.UserCredentialsBody;
import com.itis.bugtracker.BugTrackerLibImpl.models.domain.dto.AuthorizationDTO;
import com.itis.bugtracker.BugTrackerLibImpl.models.domain.dto.ResetPasswordDTO;
import com.itis.bugtracker.BugTrackerLibImpl.models.domain.dto.UserDataDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/reset")
public class AccountController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AccountController(UserService userService,
                             PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @ApiOperation(
            value = "Reset user password",
            response = UserCredentialsBody.class
    )
    @ApiResponse(code = 200, message = "Password successfully changed")
    @PutMapping
    public ResponseEntity<UserCredentialsBody> resetPassword(@RequestBody ResetPasswordDTO resetPasswordDTO) {

        User user = userService.findByUsername(resetPasswordDTO.getUsername());

        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        if (passwordEncoder.matches(resetPasswordDTO.getOldPassword(), user.getHashPassword())) {
            user.setHashPassword(passwordEncoder.encode(resetPasswordDTO.getNewPassword()));
            userService.save(user);
        }

        return ResponseEntity.ok(new UserCredentialsBody(resetPasswordDTO.getUsername(), resetPasswordDTO.getNewPassword()));
    }

    @ApiOperation(value = "Change user credentials",
            notes = "For managers only." +
                    "Param id - user id")
    @ApiResponse(code = 200, message = "Credentials successfully changed")
    @PutMapping("/credentials/{id}")
    public void changeCredentials(@RequestBody AuthorizationDTO authorizationDTO,
                                  @PathVariable("id") Long userId) {
        User user = userService.findById(userId);
        String newUsername = authorizationDTO.getUsername();
        String newPassword = authorizationDTO.getPassword();
        if (newUsername != null) {
            user.setUsername(newUsername);
        }
        if (newPassword != null) {
            user.setHashPassword(passwordEncoder.encode(authorizationDTO.getPassword()));
        }
        userService.save(user);
    }

    @ApiOperation(value = "Change user role",
            notes = "For managers only." +
                    "Param id - user id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Role successfully changed"),
            @ApiResponse(code = 400, message = "Invalid format of input data")
    })
    @PutMapping("/role/{id}")
    public void changeRole(@RequestBody @Valid UserDataDTO userDataDTO,
                           @PathVariable("id") Long userId,
                           BindingResult bindingResult,
                           HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            return;
        }
        User.Role role = User.Role.valueOf(userDataDTO.getRole());
        User user = userService.findById(userId);
        user.setRole(role);
        try {
            userService.changeRole(user, role);
        } catch (IllegalStateException e) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
    }
}
