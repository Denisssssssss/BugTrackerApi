package com.itis.bugtracker.impl.controllers;

import com.itis.bugtracker.api.security.token.TokenProvider;
import com.itis.bugtracker.api.services.UserService;
import com.itis.bugtracker.impl.models.data.User;
import com.itis.bugtracker.impl.models.domain.body.TokenBody;
import com.itis.bugtracker.impl.models.domain.dto.AuthorizationDTO;
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
@RequestMapping("/signUp")
public class SignUpController {

    private final UserService userService;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SignUpController(UserService userService,
                            TokenProvider tokenProvider,
                            PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    @ApiOperation(value = "Registration", response = TokenBody.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successfully signed up, returns access token"),
            @ApiResponse(code = 400, message = "Specified username already taken")
    })
    @PostMapping
    public ResponseEntity<TokenBody> signUp(@RequestBody AuthorizationDTO authorizationDTO) {

        String username = authorizationDTO.getUsername();
        String password = authorizationDTO.getPassword();

        if (userService.findByUsername(username) == null) {
            User user = userService.save(User.builder()
                    .username(username)
                    .hashPassword(passwordEncoder.encode(password))
                    .build()
            );
            String token = tokenProvider.generate(user.getId());
            return ResponseEntity.ok(new TokenBody(token));
        }

        return ResponseEntity.badRequest().build();
    }
}