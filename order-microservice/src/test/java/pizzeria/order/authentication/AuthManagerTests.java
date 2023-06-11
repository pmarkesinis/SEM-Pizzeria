package pizzeria.order.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import pizzeria.order.authentication.AuthManager;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthManagerTests {
    private transient AuthManager authManager;

    @BeforeEach
    public void setup() {
        authManager = new AuthManager();
    }

    @Test
    public void getNetidTest() {
        // Arrange
        String expected = "user123";
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                expected,
                null, Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        // Act
        String actual = authManager.getNetId();

        String actualRole = authManager.getRole();

        // Assert
        assertThat(actual).isEqualTo(expected);

        assertThat(actualRole).isEqualTo("[ROLE_ADMIN]");
    }

    @Test
    public void getRolesTest() {
        // Arrange
        String expected = "user123";
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                expected,
                null, Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);


        String actualRole = authManager.getRole();

        assertThat(actualRole).isEqualTo("[ROLE_ADMIN]");
    }
}
