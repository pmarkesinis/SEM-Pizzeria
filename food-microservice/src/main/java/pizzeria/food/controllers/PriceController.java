package pizzeria.food.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pizzeria.food.domain.ingredient.IngredientNotFoundException;
import pizzeria.food.domain.ingredient.IngredientService;
import pizzeria.food.domain.recipe.RecipeNotFoundException;
import pizzeria.food.domain.recipe.RecipeService;
import pizzeria.food.domain.recipe.RecipeServiceResponseInformation;
import pizzeria.food.models.prices.GetPricesRequestModel;
import pizzeria.food.models.prices.GetPricesResponseModel;
import pizzeria.food.models.prices.Tuple;

import java.util.Map;

@RestController
@RequestMapping("/price")
public class PriceController {
    private final transient RecipeService recipeService;
    private final transient IngredientService ingredientService;
    private final transient RecipeServiceResponseInformation recipeServiceResponseInformation;

    /**
     * Constructor for the PriceController that auto wires the required databases
     * @param recipeService RecipeService that handles all the recipe operations.
     * @param ingredientService IngredientService that handles all the ingredient operations.
     */
    @Autowired
    public PriceController(RecipeService recipeService,
                           IngredientService ingredientService,
                           RecipeServiceResponseInformation recipeServiceResponseInformation){
        this.recipeService = recipeService;
        this.ingredientService = ingredientService;
        this.recipeServiceResponseInformation = recipeServiceResponseInformation;
    }

    /**
     * @param requestModel GetPricesRequestModel that holds the ids of the recipes and ingredients
     * from which we want the prices
     * @return two list of doubles representing the prices of the requested recipes and ingredients
     */
    @PostMapping("/ids")
    public ResponseEntity<GetPricesResponseModel> getPrices(@RequestBody GetPricesRequestModel requestModel) {
        try {

            //System.out.println(requestModel.getFoodIds() + " " + requestModel.getIngredientIds());
            Map<Long, Tuple> foodPrices = recipeServiceResponseInformation.getPrices(requestModel.getFoodIds());
            Map<Long, Tuple> ingredientPrices = ingredientService.getDetails(requestModel.getIngredientIds());
            GetPricesResponseModel responseModel = new GetPricesResponseModel();
            responseModel.setFoodPrices(foodPrices);
            responseModel.setIngredientPrices(ingredientPrices);

            return ResponseEntity.status(HttpStatus.OK).body(responseModel);
        } catch (RecipeNotFoundException | IngredientNotFoundException e) {
            return ResponseEntity.badRequest().header(HttpHeaders.WARNING, e.getMessage()).build();
        }

    }
}
