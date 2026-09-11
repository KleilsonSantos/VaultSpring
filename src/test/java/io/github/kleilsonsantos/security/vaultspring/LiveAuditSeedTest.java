package io.github.kleilsonsantos.security.vaultspring;

import io.github.kleilsonsantos.security.vaultspring.entity.User;
import io.github.kleilsonsantos.security.vaultspring.repository.UserRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

/**
 * Seeds a known dev user for live curl audits against localhost:8080.
 * Excluded from default Surefire (tag {@code live}) — requires PostgreSQL dev profile.
 * Run manually: {@code ./mvnw test -Dgroups=live} with Postgres up.
 */
@Tag("live")
@SpringBootTest
@ActiveProfiles("dev")
class LiveAuditSeedTest {

    static final String EMAIL = "live-audit@example.com";

    static final String PASSWORD = "secret123";

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void seedLiveAuditUser() {
        userRepository.findByEmail(EMAIL).orElseGet(() -> {
            User user = new User();
            user.setName("Live Audit");
            user.setEmail(EMAIL);
            user.setPassword(passwordEncoder.encode(PASSWORD));
            return userRepository.save(user);
        });
        System.out.println("LIVE_AUDIT_USER=" + EMAIL);
        System.out.println("LIVE_AUDIT_PASSWORD=" + PASSWORD);
    }
}
