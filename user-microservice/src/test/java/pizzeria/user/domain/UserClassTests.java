package pizzeria.user.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pizzeria.user.domain.user.User;
import pizzeria.user.domain.user.UserRepository;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserClassTests {

    @Autowired
    private transient UserRepository userRepository;

    private String email, other_email, name, other_name;
    private List<String> allergies, other_allergies;

    @BeforeEach
    public void init() {
        email = "Test1@gmail.com";
        other_email = "other_email";
        name = "Test1";
        other_name = "other_name";
        allergies = List.of("Al1", "Al2", "Al3");
        other_allergies = List.of("other_allergy1", "other_allergy2");
    }

    @Test
    public void userEquals_worksCorrectly() {
        User user = new User(name, email, allergies);
        //generate the id
        userRepository.save(user);
        String userId = user.getId();
        User newUser = new User(other_name, other_email, other_allergies);
        newUser.setId(userId);

        assertThat(user.equals(newUser)).isTrue();
    }
    @Test
    public void userEquals_isNotEqual() {
        User user = new User(name, email, allergies);
        //generate the id
        userRepository.save(user);
        String userId = user.getId();
        User newUser = new User(other_name, other_email, other_allergies);

        assertThat(user.equals(newUser)).isFalse();
    }

    @Test
    public void userEquals_AttributesButNotId() {
        User user = new User(name, email, allergies);
        //generate the id
        userRepository.save(user);
        String userId = user.getId();
        User newUser = new User(name, email, allergies);

        assertThat(user.equals(newUser)).isFalse();
    }

    @Test
    public void userEquals_compareToNotUser(){
        User user = new User(name, email, allergies);
        //generate the id
        userRepository.save(user);
        Object userId = user.getId();

        assertThat(user.equals(userId)).isFalse();
    }

    @Test
    public void userEquals_isEqualToItself() {
        User user = new User(name, email, allergies);
        //generate the id
        userRepository.save(user);

        assertThat(user.equals(user)).isTrue();
    }

    @Test
    public void userEquals_isNotEqualToNull() {
        User user = new User(name, email, allergies);
        //generate the id
        userRepository.save(user);

        assertThat(user.equals(null)).isFalse();
    }

    @Test
    public void hashCode_worksCorrectly() {
        User user = new User(name, email, allergies);
        //generate the id
        userRepository.save(user);
        assertThat(user.hashCode()).isEqualTo(Objects.hash(user.getId()));
    }
}
