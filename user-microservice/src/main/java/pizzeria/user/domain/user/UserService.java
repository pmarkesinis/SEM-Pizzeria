package pizzeria.user.domain.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pizzeria.user.communication.HttpRequestService;
import pizzeria.user.models.UserRegisterModel;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class UserService {
    private final transient UserRepository userRepository;

    private final transient HttpRequestService httpRequestService;

    @Autowired
    public UserService(UserRepository userRepository, HttpRequestService httpRequestService) {
        this.userRepository = userRepository;
        this.httpRequestService = httpRequestService;
    }

    /**
     * Save a user given UserModel containing the necessary information
     *
     * @param user UserModel containing information about the user
     */
    public void saveUser(UserRegisterModel user) throws EmailAlreadyInUseException, InvalidEmailException {
        if (!verifyEmailFormat(user.getEmail())){
            throw new InvalidEmailException(user.getEmail());
        }
        User userToSave = user.parseToUser();

        if (checkUniqueEmail(userToSave.getEmail())) {
            userRepository.save(userToSave);
        } else {
            throw new EmailAlreadyInUseException(user.getEmail());
        }
    }

    private boolean verifyEmailFormat(String testEmail) {
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(regexPattern);
        return pattern.matcher(testEmail).matches();
    }

    public ResponseEntity addUser(UserRegisterModel user) throws InvalidEmailException, EmailAlreadyInUseException {
        saveUser(user);

        Optional<User> savedUser = findUserByEmail(user.getEmail());

        //registers the user in the authenticate-microservice database
        if (!httpRequestService.registerUser(savedUser.get(), user.getPassword())) {
            deleteUserByEmail(savedUser.get().getEmail());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header(HttpHeaders.WARNING,
                    "Could not communicate with " +
                            "authentication service").build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private boolean checkUniqueEmail(String email) {
        return !userRepository.existsByEmail(email);
    }

    /**
     * Finds the user given his unique email
     *
     * @param mail the user's unique email
     * @return Optional that contains the user in the case he exists in the database
     */
    public Optional<User> findUserByEmail(String mail) {
        return userRepository.findUserByEmail(mail);
    }

    /**
     * Returns all the users in our database
     *
     * @return List containing all the users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Deletes a user from the database, given his id
     *
     * @param email Email of the user
     */
    public void deleteUserByEmail(String email) {
        userRepository.deleteUserByEmail(email);
    }


    /**
     * Deletes a user from the database, given his id
     *
     * @param id ID of the user
     */
    public void deleteUserById(String id) {
        userRepository.deleteById(id);
    }


    /**
     * Updates the allergies associated with the user with the given id
     *
     * @param id ID of the user
     * @param allergies The new allergies of the user
     * @return True or False depending on whether the user was found
     */
    public void updateUserAllergies(String id, List<String> allergies) {
        Optional<User> optionalUser = userRepository.findUserById(id);

        User currentUser = optionalUser.get();

        currentUser.setAllergies(allergies);

        userRepository.save(currentUser);
    }

    /**
     * Returns all the allergies associated with the given user
     *
     * @param id ID of the user
     * @return List of all the allergies of the current user
     */
    public List<String> getAllergies(String id) {
        Optional<User> optionalUser = userRepository.findUserById(id);

        if (optionalUser.isEmpty()) {
            return null;
        }

        User currentUser = optionalUser.get();

        return currentUser.getAllergies();
    }

    /**
     * Checks whether a user exists given his id
     * @param id unique id of the user
     * @return True or False depending on whether the user exists
     */
    public boolean userExistsById(String id) {
        return userRepository.existsById(id);
    }

    public class InvalidEmailException extends Exception {
        static final long serialVersionUID = 1L;
        public InvalidEmailException(String email) {
            super("The email " + email + " is not valid");
        }
    }
}


