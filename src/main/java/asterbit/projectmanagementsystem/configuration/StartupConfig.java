package asterbit.projectmanagementsystem.configuration;

import asterbit.projectmanagementsystem.management.user.model.entity.User;
import asterbit.projectmanagementsystem.management.user.model.enums.Role;
import asterbit.projectmanagementsystem.management.user.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class StartupConfig implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(StartupConfig.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${ADMIN_PASSWORD:admin123}")
    private String adminPassword;

    public StartupConfig(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        String email = "admin@example.com";

        if (userRepository.existsByEmail(email)) {
            log.info("Admin user already exists: {}", email);
            return;
        }

        User admin = User.builder()
                .email(email)
                .password(passwordEncoder.encode(adminPassword))
                .role(Role.ADMIN)
                .publicId(UUID.randomUUID())
                .createDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        userRepository.save(admin);
        log.info("Seeded admin user: {} (default password set)", email);
    }
}
