package asterbit.projectmanagementsystem.authentication.controller;

import asterbit.projectmanagementsystem.authentication.model.request.LoginRequest;
import asterbit.projectmanagementsystem.authentication.model.request.RegistrationRequest;
import asterbit.projectmanagementsystem.authentication.model.response.AuthorizationResponse;
import asterbit.projectmanagementsystem.authentication.service.impl.AuthenticationServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs")
public class AuthenticationController {

    private final AuthenticationServiceImpl authenticationService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with USER role by default")
    public ResponseEntity<AuthorizationResponse> register(@Valid @RequestBody RegistrationRequest request) {
        AuthorizationResponse response = authenticationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates user and returns JWT token")
    public ResponseEntity<AuthorizationResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthorizationResponse response = authenticationService.login(request);
        return ResponseEntity.ok(response);
    }
}
