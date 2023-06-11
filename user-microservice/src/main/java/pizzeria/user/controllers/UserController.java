package pizzeria.user.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import pizzeria.user.authentication.AuthManager;
import pizzeria.user.communication.HttpRequestService;
import pizzeria.user.domain.user.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import pizzeria.user.models.*;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    private final transient UserService userService;

    private final transient HttpRequestService httpRequestService;

    private final transient AuthManager authManager;

    /**
     * Dependency injection
     * @param userService User service
     * @param httpRequestService Http service used to send http requests
     * @param authManager Authentication manager from which we can get information about the current http request user
     */
    @Autowired
    public UserController(UserService userService, HttpRequestService httpRequestService, AuthManager authManager) {
        this.userService = userService;
        this.httpRequestService = httpRequestService;
        this.authManager = authManager;
    }

    private boolean checkUserCreationModel(UserRegisterModel user) {
        return user.getEmail() == null || user.getPassword() == null || user.getName() == null ||
                user.getEmail().isEmpty() || user.getPassword().isEmpty() || user.getName().isEmpty();
    }

    /**
     * Endpoint used for creating new users
     * @param user UserModel which contains the following fields [email, role, allergies, name, password]
     * @return A response indicating either failure or success
     */
    @PostMapping("/create_user")
    public ResponseEntity create(@RequestBody UserRegisterModel user) {
        // perform UserModel data validation
        if (checkUserCreationModel(user)) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).header(HttpHeaders.WARNING,
                    "Arguments for user are " +
                    "invalid").build();
        }

        try {
            return userService.addUser(user);
        } catch (EmailAlreadyInUseException | UserService.InvalidEmailException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).header(HttpHeaders.WARNING, e.getMessage()).build();
        }
    }

    /*
    @GetMapping("/get_users")
    public List<User> getUsers() {
        try {
            return userService.getAllUsers();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }*/

    /**
     * A method that deletes a user from the repository if the user has provided a valid JWT
     * @return Response indicating whether the user account has been deleted
     */
    @DeleteMapping("/delete_user")
    public ResponseEntity deleteUser() {
        if (userService.userExistsById(authManager.getNetId())) {
            userService.deleteUserById(authManager.getNetId());
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).header(HttpHeaders.WARNING, "No user with such id found").build();
    }

    /**
     * Returns the jwt token associated with the user with the given email address,
     * provided we have the correct password
     *
     * @param loginModel Email and password for the user
     * @return If we successfully authenticate, we get the correct jwt token
     */
    @GetMapping("/login")
    public ResponseEntity<LoginResponseModel> loginUser(@RequestBody LoginModel loginModel) {
        if (loginModel.getEmail() == null || loginModel.getPassword() == null ||
                loginModel.getEmail().isEmpty() || loginModel.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).header(HttpHeaders.WARNING, "Login details' format is " + "invalid").build();
        }

        Optional<User> user = userService.findUserByEmail(loginModel.getEmail());

        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header(HttpHeaders.WARNING, "User with such email not found").build();
        }

        Optional <String> jwtToken = httpRequestService.loginUser(user.get().getId(), loginModel.getPassword());
        if (jwtToken.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header(HttpHeaders.WARNING, "Could not authenticate").build();
        }
        return ResponseEntity.ok().body(new LoginResponseModel(jwtToken.get()));
    }
}
