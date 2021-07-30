package com.itis.bugtracker.BugTrackerLibImpl.controllers;

import com.itis.bugtracker.BugTrackerLibApi.security.token.TokenProvider;
import com.itis.bugtracker.BugTrackerLibApi.services.UserService;
import com.itis.bugtracker.BugTrackerLibImpl.models.data.User;
import com.itis.bugtracker.BugTrackerLibImpl.models.domain.body.TokenBody;
import com.itis.bugtracker.BugTrackerLibImpl.models.domain.dto.AuthorizationDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/signIn")
public class SignInController {

    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SignInController(UserService userService,
                            TokenProvider tokenProvider,
                            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @ApiOperation(value = "Authorization", response = TokenBody.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully signed in, returns access token"),
            @ApiResponse(code = 400, message = "User with specified username doesn't exist")
    }
    )
    @PostMapping
    public ResponseEntity<TokenBody> signIn(@RequestBody AuthorizationDTO authorizationDTO) {

        User user = userService.findByUsername(authorizationDTO.getUsername());
        if (user == null) {
            return ResponseEntity.badRequest().build();
        }

        if (passwordEncoder.matches(authorizationDTO.getPassword(), user.getHashPassword())) {
            return ResponseEntity.ok(new TokenBody(tokenProvider.generate(user.getId())));
        } else {
            return ResponseEntity.badRequest().build();
        }
    }
}
