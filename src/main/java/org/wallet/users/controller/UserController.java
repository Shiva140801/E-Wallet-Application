package org.wallet.users.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.wallet.users.model.*;
import org.wallet.users.service.UserService;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginRequest loginRequest) {
        String token = userService.login(loginRequest);

        ApiResponse<String> response = ApiResponse.success(token, "Login successful");

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }


    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<User>> signup(@Valid @RequestBody SignupRequest signupRequest) {
        User user = userService.signup(signupRequest);
        ApiResponse<User> response = ApiResponse.created(user, "SignUp successful");
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
