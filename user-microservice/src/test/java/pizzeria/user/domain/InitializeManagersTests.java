package pizzeria.user.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pizzeria.user.communication.HttpRequestService;
import pizzeria.user.domain.user.User;
import pizzeria.user.domain.user.UserRepository;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test2", "mockHttpRequestService"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class InitializeManagersTests {
    @Autowired
    private transient UserRepository userRepository;

    @Autowired
    private transient HttpRequestService httpRequestService;

    @BeforeEach
    public void init() {
        when(httpRequestService.registerUser(any(), any())).thenReturn(true);
    }

    @Test
    public void initializeManagers_worksCorrectly() {
        User[] users = {
                new User("User1", "managerEmail@gmail.com", List.of()),
                new User("User2", "managerEmail2@gmail.com", List.of()),
                new User("User3", "managerEmail3@gmail.com", List.of()),
                new User("User4", "managerEmail4@gmail.com", List.of()),
                new User("User5", "managerEmail5@gmail.com", List.of())
        };
        for (User user : users) {
            assertThat(userRepository.existsByEmail(user.getEmail())).isTrue();
        }
    }

    @Test
    public void initializeManagersWithDifferentEmails_worksCorrectly() {
        User[] users = {
                new User("User1", "managerEmai12l@gmail.com", List.of()),
                new User("User2", "manag51erEmail2@gmail.com", List.of()),
                new User("User3", "managerEmail6@gmail.com", List.of()),
                new User("User4", "managerEmail112@gmail.com", List.of()),
                new User("User5", "notManagerEmail5@gmail.com", List.of())
        };
        for (User user : users) {
            assertThat(userRepository.existsByEmail(user.getEmail())).isFalse();
        }
    }

    @Test
    public void sizeOfRecords_isCorrect() {
        List<User> usersInsideRepo = userRepository.findAll();

        assertThat(usersInsideRepo.size() == 5);
    }
}
