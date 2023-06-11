package pizzeria.authentication.domain.user;

import org.springframework.stereotype.Service;

/**
 * A DDD service for registering a new user.
 */
@Service
public class RegistrationService {
    private final transient UserRepository userRepository;
    private final transient PasswordHashingService passwordHashingService;
    private final transient int MANAGER_ACCOUNTS = 5;

    /**
     * Instantiates a new UserService.
     *
     * @param userRepository  the user repository
     * @param passwordHashingService the password encoder
     */
    public RegistrationService(UserRepository userRepository, PasswordHashingService passwordHashingService) {
        this.userRepository = userRepository;
        this.passwordHashingService = passwordHashingService;
    }

    /**
     * Register a new user.
     *
     * @param id    The NetID of the user
     * @param password The password of the user
     * @throws Exception if the user already exists
     */
    public AppUser registerUser(String id, Password password) throws Exception {

        if (checkIdIsUnique(id)) {
            // Hash password
            HashedPassword hashedPassword = passwordHashingService.hash(password);

            if (userRepository.count() < MANAGER_ACCOUNTS) {
                // create new manager account
                AppUser manager = AppUser.createManager(id, hashedPassword);
                userRepository.save(manager);

                //System.out.println("the role of the new user is: " + manager.getRole());
                return manager;
            } else {
                // Create new account
                AppUser user = new AppUser(id, hashedPassword);

                userRepository.save(user);

                //System.out.println("the role of the new user is: " + user.getRole());
                return user;
            }
        }

        throw new IdAlreadyInUseException(id);
    }

    public boolean checkIdIsUnique(String id) {
        return !userRepository.existsById(id);
    }
}
