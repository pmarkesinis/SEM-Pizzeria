package pizzeria.food.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pizzeria.food.communication.HttpRequestService;
import pizzeria.food.domain.Allergens.AllergenService;
import pizzeria.food.models.allergens.CheckIfRecipeIsSafeRequestModel;
import pizzeria.food.models.allergens.FilterMenuResponseModel;
import java.util.Optional;

@RestController
@RequestMapping("/allergens")
public class AllergenController {

    private final transient AllergenService allergenService;
    private final transient HttpRequestService requestService;

    /**
     * Constructor for the AllergenController class that auto wires the required service
     * @param allergenService AllergenService that handles the allergen complexity
     */
    @Autowired
    public AllergenController(AllergenService allergenService, HttpRequestService requestService) {
        this.allergenService = allergenService;
        this.requestService = requestService;
    }


    /**
     * @param token The token of the user
     * @return List of recipes that don't contain the specified allergens
     */
    @GetMapping("/menu")
    public ResponseEntity<FilterMenuResponseModel> filterMenu(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            FilterMenuResponseModel responseModel = allergenService.filterMenu(token);
            if (responseModel == null) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            return ResponseEntity.status(HttpStatus.OK).body(allergenService.filterMenu(token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().header(HttpHeaders.WARNING, e.getMessage()).build();
        }
    }

    @GetMapping("/warn")
    public ResponseEntity<Boolean> checkIfSafe(@RequestHeader(HttpHeaders.AUTHORIZATION) String token, @RequestBody CheckIfRecipeIsSafeRequestModel requestModel) {
        try {
            Optional<Boolean> checkSafetyStatus = allergenService.checkSafety(token, requestModel);
            if (checkSafetyStatus.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
            return ResponseEntity.status(HttpStatus.OK).body(checkSafetyStatus.get());

        } catch (Exception e) {
            return ResponseEntity.badRequest().header(HttpHeaders.WARNING, e.getMessage()).build();
        }
    }
}
