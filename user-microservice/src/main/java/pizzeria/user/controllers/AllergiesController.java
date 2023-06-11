package pizzeria.user.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pizzeria.user.authentication.AuthManager;
import pizzeria.user.domain.user.UserService;
import pizzeria.user.models.AllergiesModel;
import pizzeria.user.models.AllergiesResponseModel;

import java.util.List;
@RestController
@RequestMapping("/allergies")
public class AllergiesController {
    private final transient UserService userService;

    private final transient AuthManager authManager;

    /**
     * Dependency injection
     * @param userService User service
     * @param authManager Authentication manager from which we can get information about the current http request user
     */
    @Autowired
    public AllergiesController(UserService userService, AuthManager authManager) {
        this.userService = userService;
        this.authManager = authManager;
    }

    /**
     * Endpoint which allows changing of allergies for a given user. The id user is extracted from the JWT
     * token that is used for authentication
     * @param allergiesModel AllergiesModel which contains a list of the new allergies
     * @return A response indicating either failure or success
     */
    @PutMapping("/update_allergies")
    public ResponseEntity updateAllergies(@RequestBody AllergiesModel allergiesModel) {
        if (userService.userExistsById(authManager.getNetId())) {
            if (allergiesModel.getAllergies() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).header(HttpHeaders.WARNING, "Allergens are null").build();
            }

            userService.updateUserAllergies(authManager.getNetId(), allergiesModel.getAllergies());
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header(HttpHeaders.WARNING, "User with such id not found").build();
        }
    }

    /**
     * Endpoint which returns all the allergies associated with a user in our database.
     * The user id for which we want the allergies is extracted from the JWT token
     * used for authentication
     *
     * @return A response indicating either failure or success and a list with allergies in the body
     */
    @GetMapping("/get_allergies")
    public ResponseEntity<AllergiesResponseModel> getAllergies() {
        if (userService.userExistsById(authManager.getNetId())) {
            List<String> allergies = userService.getAllergies(authManager.getNetId());

            return ResponseEntity.ok().body(new AllergiesResponseModel(allergies));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header(HttpHeaders.WARNING, "User with such id not found").build();
        }
    }
}
