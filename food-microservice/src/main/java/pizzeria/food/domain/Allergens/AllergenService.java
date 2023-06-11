package pizzeria.food.domain.Allergens;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pizzeria.food.communication.HttpRequestService;
import pizzeria.food.domain.ingredient.IngredientNotFoundException;
import pizzeria.food.domain.ingredient.IngredientRepository;
import pizzeria.food.domain.recipe.Recipe;
import pizzeria.food.domain.recipe.RecipeNotFoundException;
import pizzeria.food.domain.recipe.RecipeRepository;
import pizzeria.food.models.allergens.CheckIfRecipeIsSafeRequestModel;
import pizzeria.food.models.allergens.FilterMenuResponseModel;

import java.util.*;

@Service
public class AllergenService {
    private final transient RecipeRepository recipeRepository;
    private final transient IngredientRepository ingredientRepository;
    private final transient HttpRequestService requestService;


    /**
     * Constructor for the AllergenService class that auto wires the required databases
     * @param recipeRepository RecipeRepository in which we will perform all recipe related operations
     * @param ingredientRepository IngredientRepository in which we will perform all ingredient related operations
     */
    @Autowired
    public AllergenService(RecipeRepository recipeRepository,
                           IngredientRepository ingredientRepository,
                           HttpRequestService requestService) {
        this.recipeRepository = recipeRepository;
        this.ingredientRepository = ingredientRepository;
        this.requestService = requestService;
    }

    /**
     * @param allergens list of strings that represents the allergens we need to filter on
     * @return a list of recipe that does not contain any of the specified allergies
     * @throws IngredientNotFoundException when an ingredient of a recipe wasn't found in the database
     */
    public List<Recipe> filterMenuOnAllergens(List<String> allergens) throws IngredientNotFoundException {
        List<Recipe> recipes = recipeRepository.findAll();
        List<Recipe> menu = new ArrayList<>();
        for (Recipe recipe: recipes){
            if (recipeIsSafe(recipe, allergens)){
                menu.add(recipe);
            }
        }
        return menu;
    }


    /**
     * @param recipe Recipe we want to check for allergens
     * @param allergens list of strings that represents the allergens
     * @return true iff the recipe does not contain any of the allergens
     * @throws IngredientNotFoundException when an ingredient of this recipe is not stored in the database
     */
    public boolean recipeIsSafe(Recipe recipe, List<String> allergens) throws IngredientNotFoundException {
        List<Long> ids = recipe.getBaseToppings();
        Set <String> recipeAllergens = getSetOfAllergens(ids);
        recipeAllergens.retainAll(new HashSet<>(allergens));
        return recipeAllergens.isEmpty();
    }

    /**
     * Returns a set of the allergens contained in the list of ids
     * @param ingredientIds list of ingredient ids
     * @return set of allergens
     * @throws IngredientNotFoundException
     */
    @SuppressWarnings("PMD")
    private Set<String> getSetOfAllergens(List<Long> ingredientIds) throws IngredientNotFoundException {
        Set<String> allergens = new HashSet<>();

        for (Long ingredientId : ingredientIds) {
            if (!ingredientRepository.existsById(ingredientId)) {
                throw new IngredientNotFoundException();
            }
            allergens.addAll(ingredientRepository.findById(ingredientId).get().getAllergens());
        }
        return allergens;
    }

    /**
     * @param recipeId id of the recipe we want to check for allergens
     * @param allergens list of strings that represents the allergens
     * @return true iff the recipe does not contain any of the specified allergens
     * @throws RecipeNotFoundException when the recipe is not stored in the database
     * @throws IngredientNotFoundException when an ingredient of this recipe is not stored in the database
     */
    public boolean checkIfSafeRecipeWithId(long recipeId, List<String> allergens) throws RecipeNotFoundException, IngredientNotFoundException {
        if (recipeRepository.existsById(recipeId)){
            Recipe recipe = recipeRepository.findById(recipeId).get();
            return recipeIsSafe(recipe, allergens);
        } else {
            throw new RecipeNotFoundException();
        }
    }

    public FilterMenuResponseModel filterMenu(String token) throws IngredientNotFoundException {
        Optional<List<String>> allergens = requestService.getUserAllergens(token);
        if (allergens.isPresent()) {
            List<Recipe> filteredMenu = filterMenuOnAllergens(allergens.get());
            FilterMenuResponseModel responseModel = new FilterMenuResponseModel();
            responseModel.setRecipes(filteredMenu);
            return responseModel;
        }
        return null;
    }

    public Optional<Boolean> checkSafety(String token, CheckIfRecipeIsSafeRequestModel requestModel) throws Exception {
        Optional<List<String>> allergens = requestService.getUserAllergens(token);

        if (allergens.isPresent()) {
            boolean checkSafety = checkIfSafeRecipeWithId(requestModel.getId(), allergens.get());

            return Optional.of(checkSafety);
        }
        return Optional.empty();
    }
}
