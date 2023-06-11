package pizzeria.food.authentication;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;


class AuthManagerTest {

    private transient AuthManager authManager;

    @BeforeEach
    public void setup() {
        authManager = new AuthManager();
    }

    @Test
    void getNetId() {
        String expected = "user123";
        var authenticationToken = new UsernamePasswordAuthenticationToken(
                expected,
                null, Collections.singleton(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        String actual = authManager.getNetId();
        String actualRole = authManager.getRole();
        assertThat(actual).isEqualTo(expected);
        assertThat(actualRole).isEqualTo("[ROLE_ADMIN]");
    }

    @Test
    void getRole() {
        String expected = "user123";
        var authenticationToken = new UsernamePasswordAuthenticationToken(
                expected,
                null, Collections.singleton(new SimpleGrantedAuthority("ROLE_MANAGER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);


        String actualRole = authManager.getRole();

        assertThat(actualRole).isEqualTo("[ROLE_MANAGER]");
    }
}