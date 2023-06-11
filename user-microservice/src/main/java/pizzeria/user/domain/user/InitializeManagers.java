package pizzeria.user.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pizzeria.user.communication.HttpRequestService;
import javax.annotation.PostConstruct;
import java.util.List;

@Component
@Profile("!test")
public class InitializeManagers {
    private transient UserRepository userRepository;
    private transient HttpRequestService httpRequestService;

    @Autowired
    public InitializeManagers(UserRepository userRepository, HttpRequestService httpRequestService) {
        this.userRepository = userRepository;
        this.httpRequestService = httpRequestService;
    }

    @SuppressWarnings("PMD")
    @PostConstruct
    private void initializeManagers() {
        User[] users = {
                new User("User1", "managerEmail@gmail.com", List.of()),
                new User("User2", "managerEmail2@gmail.com", List.of()),
                new User("User3", "managerEmail3@gmail.com", List.of()),
                new User("User4", "managerEmail4@gmail.com", List.of()),
                new User("User5", "managerEmail5@gmail.com", List.of())
        };

        String[] passwords = {"pass1", "pass2", "pass3", "pass4", "pass5"};

        for (int i = 0; i < 5; i++) {
            userRepository.save(users[i]);

            System.out.println(httpRequestService.registerUser(users[i], passwords[i]));
        }
    }
}
