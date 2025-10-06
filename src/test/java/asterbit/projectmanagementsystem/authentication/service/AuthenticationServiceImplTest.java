package asterbit.projectmanagementsystem.authentication.service;

import asterbit.projectmanagementsystem.authentication.model.request.LoginRequest;
import asterbit.projectmanagementsystem.authentication.model.request.RegistrationRequest;
import asterbit.projectmanagementsystem.authentication.model.response.AuthorizationResponse;
import asterbit.projectmanagementsystem.authentication.service.impl.AuthenticationServiceImpl;
import asterbit.projectmanagementsystem.management.user.model.entity.User;
import asterbit.projectmanagementsystem.management.user.model.enums.Role;
import asterbit.projectmanagementsystem.management.user.repository.UserRepository;
import asterbit.projectmanagementsystem.security.service.JwtGeneratorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private BCryptPasswordEncoder encoder;
    @Mock private JwtGeneratorService jwtGeneratorService;

    @InjectMocks private AuthenticationServiceImpl service;

    @Test
    void register_createsUser_andReturnsToken() {
        RegistrationRequest req = new RegistrationRequest("user@example.com", "pass", "pass");
        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(encoder.encode("pass")).thenReturn("enc");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtGeneratorService.generateToken(any(UUID.class), eq(Role.USER))).thenReturn("jwt");
        when(jwtGeneratorService.getExpirationMinutes()).thenReturn("60 minutes");

        AuthorizationResponse resp = service.register(req);

        assertThat(resp.token()).isEqualTo("jwt");
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getRole()).isEqualTo(Role.USER);
    }

    @Test
    void login_returnsToken_onValidCredentials() {
        LoginRequest req = new LoginRequest("user@example.com", "pass");
        User user = User.builder()
                .id(1L)
                .email("user@example.com")
                .publicId(UUID.randomUUID())
                .password("enc")
                .role(Role.USER)
                .createDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(encoder.matches("pass", "enc")).thenReturn(true);
        when(jwtGeneratorService.generateToken(any(UUID.class), eq(Role.USER))).thenReturn("jwt");
        when(jwtGeneratorService.getExpirationMinutes()).thenReturn("60 minutes");

        AuthorizationResponse resp = service.login(req);
        assertThat(resp.token()).isEqualTo("jwt");
    }

    @Test
    void register_throwsWhenEmailExists() {
        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);
        RegistrationRequest req = new RegistrationRequest("user@example.com", "a", "a");
        assertThrows(IllegalArgumentException.class, () -> service.register(req));
    }

    @Test
    void register_throwsWhenPasswordsDoNotMatch() {
        RegistrationRequest req = new RegistrationRequest("user@example.com", "a", "b");
        assertThrows(IllegalArgumentException.class, () -> service.register(req));
    }

    @Test
    void login_throwsWhenUserNotFound() {
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
        LoginRequest req = new LoginRequest("user@example.com", "x");
        assertThrows(IllegalArgumentException.class, () -> service.login(req));
    }

    @Test
    void login_throwsWhenPasswordInvalid() {
        User user = User.builder().id(1L).email("user@example.com").password("enc").role(Role.USER).build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(encoder.matches("wrong", "enc")).thenReturn(false);
        LoginRequest req = new LoginRequest("user@example.com", "wrong");
        assertThrows(IllegalArgumentException.class, () -> service.login(req));
    }
}
