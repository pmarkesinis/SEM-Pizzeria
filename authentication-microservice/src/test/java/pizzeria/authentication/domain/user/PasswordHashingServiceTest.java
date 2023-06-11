package pizzeria.authentication.domain.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class PasswordHashingServiceTest {
    @Test
    void testHash() {
        BCryptPasswordEncoder enc = new BCryptPasswordEncoder();
        PasswordHashingService serv = new PasswordHashingService(enc);
        Password hp = new Password("pass");

        assertEquals(serv.hash(hp), new HashedPassword(enc.encode("pass")));
    }
}
