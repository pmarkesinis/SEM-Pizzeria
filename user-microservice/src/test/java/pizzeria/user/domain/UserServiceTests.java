package pizzeria.user.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pizzeria.user.domain.user.EmailAlreadyInUseException;
import pizzeria.user.domain.user.User;
import pizzeria.user.domain.user.UserRepository;
import pizzeria.user.domain.user.UserService;
import pizzeria.user.models.UserRegisterModel;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserServiceTests {
    @Autowired
    private transient UserService userService;

    @Autowired
    private transient UserRepository userRepository;

    private UserRegisterModel userModel;
    private String email, name, password;
    private List<String> allergies;

    @BeforeEach
    public void init() {
        email = "Test1@gmail.com";
        name = "Test1";
        password = "coolpassword";
        allergies = List.of("Al1", "Al2", "Al3");


        userModel = new UserRegisterModel();
        userModel.setEmail(email);
        userModel.setName(name);
        userModel.setPassword(password);
        userModel.setAllergies(allergies);
    }

    @Test
    public void saveUser_worksCorrectly() {
        try {
            userService.saveUser(userModel);
        } catch (EmailAlreadyInUseException | UserService.InvalidEmailException e) {
            System.out.println("User with such email already exists");
        }

        Optional<User> actualUser = userRepository.findUserByEmail(email);

        Assertions.assertThat(actualUser).isNotEmpty();

        assertThat(actualUser.get().getEmail()).isEqualTo(email);
        assertThat(actualUser.get().getName()).isEqualTo(name);
        assertThat(actualUser.get().getAllergies()).containsExactlyElementsOf(allergies);
    }

    @Test
    public void testSaveUserWrongEmailFormat(){
        userModel.setEmail("test");
        UserService.InvalidEmailException exception = assertThrows(UserService.InvalidEmailException.class, () -> {
            userService.saveUser(userModel);
        });

        assertThat(exception.getMessage()).isEqualTo("The email test is not valid");
    }

    @Test
    public void testSaveUserEmailAlreadyInUse(){
        try {
            userService.saveUser(userModel);
        } catch (EmailAlreadyInUseException | UserService.InvalidEmailException e) {
            System.out.println("User with such email already exists");
        }

        Optional<User> actualUser = userRepository.findUserByEmail(email);

        //make sure the user was saved properly
        Assertions.assertThat(actualUser).isNotEmpty();

        //now try to save the user again but the email is already in use
        EmailAlreadyInUseException exception = assertThrows(EmailAlreadyInUseException.class, () -> {
            userService.saveUser(userModel);
        });

        assertThat(exception.getMessage()).isEqualTo("Test1@gmail.com");
    }

    @Test
    public void findUserByEmail_worksCorrectly() {
        User tempUser = new User(name, email, allergies);

        userRepository.save(tempUser);

        Optional<User> actualUser = userService.findUserByEmail(email);

        Assertions.assertThat(actualUser).isNotEmpty();

        assertThat(actualUser.get()).isEqualTo(tempUser);
    }

    @Test
    public void getAllUsers_worksCorrectly() {
        User tempUser1 = new User(name, email, allergies);
        User tempUser2 = new User(name, "borislav2@gmail.com", allergies);
        User tempUser3 = new User(name, "borislav3@gmail.com", allergies);

        userRepository.save(tempUser1);
        userRepository.save(tempUser2);
        userRepository.save(tempUser3);

        List<User> getActualUsers = userService.getAllUsers();

        assertThat(getActualUsers).containsAll(List.of(tempUser1, tempUser2, tempUser3));
    }

    @Test
    public void deleteUserById_worksCorrectly() {
        userRepository.save(new User(name, email, allergies));

        assertThat(userRepository.existsByEmail(email)).isTrue();

        Optional<User> user = userService.findUserByEmail(email);

        userService.deleteUserById(user.get().getId());

        assertThat(userRepository.findUserByEmail(email)).isEmpty();
    }

    @Test
    public void deleteUserByEmail_worksCorrectly() {
        userRepository.save(new User(name, email, allergies));

        assertThat(userRepository.existsByEmail(email)).isTrue();

        Optional<User> user = userService.findUserByEmail(email);

        userService.deleteUserByEmail(user.get().getEmail());

        assertThat(userRepository.findUserByEmail(email)).isEmpty();
    }

    @Test
    public void updateUserAllergies_worksCorrectly() {
        User tempUser = new User(name, email, allergies);
        List <String> newAllergies = List.of("newAllergy1", "newAllergy2", "newAllergy3");

        userRepository.save(tempUser);

        userService.updateUserAllergies(tempUser.getId(), newAllergies);

        Optional <User> updatedUser = userRepository.findUserById(tempUser.getId());

        Assertions.assertThat(updatedUser).isNotEmpty();

        assertThat(updatedUser.get().getAllergies()).containsExactlyInAnyOrderElementsOf(newAllergies);
    }

    @Test
    public void getAllergies_worksCorrectly() {
        User tempUser = new User(name, email, allergies);

        userRepository.save(tempUser);

        Optional <User> actualUser = userRepository.findUserById(tempUser.getId());

        Assertions.assertThat(actualUser).isNotEmpty();

        assertThat(userService.getAllergies(tempUser.getId())).containsExactlyInAnyOrderElementsOf(allergies);
    }

    @Test
    public void getAllergiesOnNonExistingUser(){
        User tempUser = new User(name, email, allergies);

        userRepository.save(tempUser);

        //uuid's are 32 base-16 character strings so "test" will never be generated
        Optional <User> actualUser = userRepository.findUserById("test");

        Assertions.assertThat(actualUser).isEmpty();

        assertThat(userService.getAllergies("test")).isNull();
    }
}
