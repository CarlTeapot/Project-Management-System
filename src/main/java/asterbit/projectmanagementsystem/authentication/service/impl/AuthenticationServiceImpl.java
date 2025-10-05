package asterbit.projectmanagementsystem.authentication.service.impl;

import asterbit.projectmanagementsystem.authentication.model.request.LoginRequest;
import asterbit.projectmanagementsystem.authentication.model.request.RegistrationRequest;
import asterbit.projectmanagementsystem.authentication.model.response.AuthorizationResponse;
import asterbit.projectmanagementsystem.authentication.service.AuthenticationService;
import asterbit.projectmanagementsystem.management.user.model.entity.User;
import asterbit.projectmanagementsystem.management.user.model.enums.Role;
import asterbit.projectmanagementsystem.management.user.repository.UserRepository;
import asterbit.projectmanagementsystem.security.service.JwtGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtGeneratorService jwtGeneratorService;

    @Override
    public AuthorizationResponse register(RegistrationRequest request) {
        if (request == null || request.email() == null || request.password() == null || request.confirmPassword() == null) {
            throw new IllegalArgumentException("Email and passwords must be provided");
        }

        String email = request.email().trim().toLowerCase();
        if (email.isBlank()) {
            throw new IllegalArgumentException("Email must not be blank");
        }
        if (!request.password().equals(request.confirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("User with email " + email + " already exists");
        }

        User user = User.builder()
                .publicId(java.util.UUID.randomUUID())
                .email(email)
                .password(encoder.encode(request.password()))
                .role(Role.USER)
                .createDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        User saved = userRepository.save(user);

        String token = jwtGeneratorService.generateToken(saved.getPublicId(), saved.getRole());
        return new AuthorizationResponse(
                token,
                jwtGeneratorService.getExpirationMinutes()
        );
    }

    @Override
    public AuthorizationResponse login(LoginRequest request) {
        if (request == null || request.email() == null || request.password() == null) {
            throw new IllegalArgumentException("Email and password must be provided");
        }

        String email = request.email().trim().toLowerCase();

        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User with email " + email + " not found");
        }

        User user = userOptional.get();

        if (!encoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }

        String token = jwtGeneratorService.generateToken(user.getPublicId(), user.getRole());

        return new AuthorizationResponse(
                token,
                jwtGeneratorService.getExpirationMinutes()
        );
    }
}
