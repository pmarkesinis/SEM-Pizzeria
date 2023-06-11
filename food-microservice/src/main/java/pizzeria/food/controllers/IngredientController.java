package pizzeria.food.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pizzeria.food.domain.ingredient.*;
import pizzeria.food.models.ingredient.*;

import java.util.List;

@RestController
@RequestMapping("/ingredient")
public class IngredientController {
    private final transient IngredientService ingredientService;

    /**
     * Constructor for the IngredientController class that autowires the required service
     * @param ingredientService IngredientService that handles the complexity
     */
    @Autowired
    public IngredientController(IngredientService ingredientService){
        this.ingredientService = ingredientService;
    }

    /**
     * @param model SaveIngredientRequestModel that holds the Ingredient we want to save
     * @return the saved ingredient and its id
     */
    @PostMapping("/save")
    public ResponseEntity<SaveIngredientResponseModel> saveIngredient(@RequestBody SaveIngredientRequestModel model){
        try {
            Ingredient saved = ingredientService.registerIngredient(model.getIngredient());
            SaveIngredientResponseModel responseModel = new SaveIngredientResponseModel();
            responseModel.setId(saved.getId());
            responseModel.setIngredient(saved);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseModel);
        } catch (IngredientAlreadyInUseException | InvalidIngredientException e){
            return ResponseEntity.badRequest().header(HttpHeaders.WARNING, e.getMessage()).build();
        }
    }

    /**
     * @param model UpdateIngredientRequestModel that holds the id and the ingredient we want to store
     * @return the updated ingredient
     */
    @PostMapping("/update")
    public ResponseEntity<UpdateIngredientResponseModel> updateIngredient(@RequestBody UpdateIngredientRequestModel model){
        try {
            Ingredient updated = ingredientService.updateIngredient(model.getIngredient(), model.getId());
            UpdateIngredientResponseModel responseModel = new UpdateIngredientResponseModel();
            responseModel.setId(updated.getId());
            responseModel.setIngredient(updated);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseModel);
        } catch (IngredientNotFoundException | InvalidIngredientException e) {
            return ResponseEntity.badRequest().header(HttpHeaders.WARNING, e.getMessage()).build();
        }
    }

    /**
     * @param model DeleteIngredientRequestModel holding the id of the Ingredient we want to delete
     * @return ResponseEntity that holds nothing or the message of the thrown exception
     */
    @DeleteMapping("/delete")
    public ResponseEntity deleteIngredient(@RequestBody DeleteIngredientRequestModel model) {
        try {
            ingredientService.deleteIngredient(model.getId());
            return ResponseEntity.ok().build();
        } catch (IngredientNotFoundException e) {
            return ResponseEntity.badRequest().header(HttpHeaders.WARNING, e.getMessage()).build();
        }
    }

    /**
     * @return the list of extra toppings
     */
    @GetMapping("/extraToppings")
    public ResponseEntity<ExtraToppingsResponseModel> getExtraToppingsSet(){
        List<Ingredient> ingredientList = ingredientService.getToppingsList();
        ExtraToppingsResponseModel responseModel = new ExtraToppingsResponseModel();
        responseModel.setIngredients(ingredientList);
        return ResponseEntity.ok().body(responseModel);
    }



}
