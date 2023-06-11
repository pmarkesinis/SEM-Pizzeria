package pizzeria.food.domain.recipe;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pizzeria.food.domain.ingredient.Ingredient;
import pizzeria.food.domain.ingredient.IngredientNotFoundException;
import pizzeria.food.domain.ingredient.IngredientRepository;
import pizzeria.food.domain.ingredient.IngredientService;
import pizzeria.food.models.prices.Tuple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RecipeServiceResponseInformation {
    private transient RecipeRepository recipeRepository;
    private transient IngredientService ingredientService;
    private transient IngredientRepository ingredientRepository;

    @Autowired
    public RecipeServiceResponseInformation(RecipeRepository recipeRepository,
                                            IngredientService ingredientService,
                                            IngredientRepository ingredientRepository) {
        this.recipeRepository = recipeRepository;
        this.ingredientService = ingredientService;
        this.ingredientRepository = ingredientRepository;
    }

    /**
     * @param ids List longs representing the ids of the recipes of which we want to get the prices
     * @return a list of doubles that are the prices of the recipes
     * @throws RecipeNotFoundException thrown when one of the ids of the recipes was not in the database.
     */
    @SuppressWarnings("PMD")
    public Map<Long, Tuple> getPrices(List<Long> ids) throws RecipeNotFoundException {
        if (ids == null) {
            return new HashMap<>();
        }

        Map<Long, Tuple> prices = new HashMap<>(ids.size());
        for (long id: ids){
            if (recipeRepository.existsById(id)) {
                Recipe recipe = recipeRepository.findById(id).get();
                prices.put(id, new Tuple(recipe.getBasePrice(), recipe.getName()));
            } else {
                throw new RecipeNotFoundException("The Recipe with the id " + id + " was not found in the databases");
            }
        }
        return prices;
    }

    /**
     * @return List of recipes that represents the menu
     */
    public List<Recipe> getMenu(){
        return recipeRepository.findAll();
    }

    /**
     * given a recipe id return the associated ingredients
     * @param id long value representing the id of the recipe
     * @return List of Ingredients representing the basetoppings that are fetched from the ingredientRepository
     */
    @SuppressWarnings("PMD")
    public List<Ingredient> getBaseToppings(long id) throws RecipeNotFoundException, IngredientNotFoundException {
        if (recipeRepository.existsById(id)){
            Recipe recipe = recipeRepository.findById(id).get();
            List<Ingredient> baseToppings = new ArrayList<>();
            ingredientService.checkForIngredientsExistence(recipe.getBaseToppings());

            for (long ingredientId: recipe.getBaseToppings()){
                baseToppings.add(ingredientRepository.findById(ingredientId).get());
            }
            return ingredientRepository.findAllById(recipe.getBaseToppings());
        } else {
            throw new RecipeNotFoundException("The Recipe with the id " + id + " was not found in the databases");
        }
    }
}
