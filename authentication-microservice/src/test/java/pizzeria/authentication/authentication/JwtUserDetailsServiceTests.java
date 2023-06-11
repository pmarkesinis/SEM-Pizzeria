package pizzeria.authentication.authentication;

import static org.assertj.core.api.Assertions.*;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pizzeria.authentication.domain.user.AppUser;
import pizzeria.authentication.domain.user.HashedPassword;
import pizzeria.authentication.domain.user.UserRepository;

@ExtendWith(SpringExtension.class)
@SpringBootTest
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockPasswordEncoder"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class JwtUserDetailsServiceTests {

    @Autowired
    private transient JwtUserDetailsService jwtUserDetailsService;

    @Autowired
    private transient UserRepository userRepository;

    @Test
    public void loadUserByUsername_withValidUser_returnsCorrectUser() {
        // Arrange
        //final NetId testUser = new NetId("SomeUser");
        final String id = "testUser";
        final HashedPassword testHashedPassword = new HashedPassword("password123Hash");

        AppUser appUser = new AppUser(id, testHashedPassword);
        userRepository.save(appUser);

        // Act
        UserDetails actual = jwtUserDetailsService.loadUserByUsername(id);

        // Assert
        assertThat(actual.getUsername()).isEqualTo(id);
        assertThat(actual.getPassword()).isEqualTo(testHashedPassword.toString());
    }

    @Test
    public void loadUserByUsername_withNonexistentUser_throwsException() {
        // Arrange
        final String testNonexistentUser = "SomeUser";
        //final NetId testUser = new NetId("AnotherUser");
        final String testUser = "AnotherUser";
        final String testPasswordHash = "password123Hash";

        AppUser appUser = new AppUser(testUser, new HashedPassword(testPasswordHash));
        userRepository.save(appUser);

        // Act
        ThrowableAssert.ThrowingCallable action = () -> jwtUserDetailsService.loadUserByUsername(testNonexistentUser);

        // Assert
        assertThatExceptionOfType(UsernameNotFoundException.class)
                .isThrownBy(action);
    }
}
