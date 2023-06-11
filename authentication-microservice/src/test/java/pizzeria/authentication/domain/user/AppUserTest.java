package pizzeria.authentication.domain.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AppUserTest {
    @Test
    void testEquals() {
        String s = "string";
        AppUser user1 = new AppUser("id", new HashedPassword("hash"));
        AppUser user2 = new AppUser("id", new HashedPassword("hash"));
        AppUser user3 = new AppUser("ID", new HashedPassword("HASH"));

        assertNotEquals(user1, null);
        assertNotEquals(user1, s);
        assertEquals(user1, user1);
        assertEquals(user1, user2);
        assertNotEquals(user1, user3);
    }

    @Test
    void testChangePass() {
        AppUser user1 = new AppUser("id", new HashedPassword("hash"));
        HashedPassword hp = new HashedPassword("newHash");
        user1.changePassword(hp);

        assertEquals(user1.getPassword(), hp);
    }

    @Test
    void testHashCode() {
        AppUser user1 = new AppUser("id", new HashedPassword("hash1"));
        AppUser user2 = new AppUser("id", new HashedPassword("hash2"));

        assertEquals(user1.hashCode(), user2.hashCode());
    }
}
