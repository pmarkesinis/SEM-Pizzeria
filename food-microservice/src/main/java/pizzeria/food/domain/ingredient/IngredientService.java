package pizzeria.food.domain.ingredient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pizzeria.food.models.prices.Tuple;

import java.util.*;

@Service
public class IngredientService {
    private final transient IngredientRepository ingredientRepository;

    /**
     * Constructor for the IngredientService class that auto wires the required database
     * @param ingredientRepository IngredientRepository  in which we will perform the operations.
     */
    @Autowired
    public IngredientService(IngredientRepository ingredientRepository){
        this.ingredientRepository = ingredientRepository;
    }

    /**
     * @param ingredient Ingredient instance we want to store in the database.
     * @return Ingredient that is stored in the database.
     * @throws IngredientAlreadyInUseException thrown when the Ingredients name or id is already in the database.
     */
    public Ingredient registerIngredient(Ingredient ingredient) throws IngredientAlreadyInUseException, InvalidIngredientException {
        if (!validateIngredient(ingredient)){
            throw new InvalidIngredientException();
        }
        if (ingredientRepository.existsById(ingredient.getId()) || ingredientRepository.existsByName(ingredient.getName())) {
            throw new IngredientAlreadyInUseException();
        }
        Ingredient result = ingredientRepository.save(ingredient);
        return result;
    }

    /**
     * @param ingredient Ingredient instance that carries the updated values that we want to store in the database.
     * @param id the id of the Ingredient we want to update
     * @return updated Ingredient that is stored in the database.
     * @throws IngredientNotFoundException thrown when the given id is not associated to an ingredient is not
     * associated with an ingredient in the database.
     */
    public Ingredient updateIngredient(Ingredient ingredient, long id) throws IngredientNotFoundException, InvalidIngredientException {
        if (!validateIngredient(ingredient)){
            throw new InvalidIngredientException();
        }
        if (ingredientRepository.existsById(id)) {
            ingredient.setId(id);
            return ingredientRepository.save(ingredient);
        } else {
            throw new IngredientNotFoundException();
        }
    }

    /**
     * @param id the id of the Ingredient that we want to delete.
     * @return true iff the ingredient was deleted successfully.
     * @throws IngredientNotFoundException thrown when the given id is not associated with
     * an ingredient in the database.
     */
    public boolean deleteIngredient(long id) throws IngredientNotFoundException {
        if (ingredientRepository.existsById(id)) {
            ingredientRepository.deleteById(id);
            return true;
        }
        throw new IngredientNotFoundException();
    }

    /**
     * @param ids list of longs that represents the ids of the ingredients we want the price from.
     * @return a list of doubles that represents the prices of the given ingredient ids
     * @throws IngredientNotFoundException when one of the given ids was not associated with an
     * ingredient in the database.
     */
    @SuppressWarnings("PMD")
    public Map<Long, Tuple> getDetails(List<Long> ids) throws IngredientNotFoundException {
        if (ids == null) {
            return new HashMap<>();
        }
        Map<Long, Tuple> prices = new HashMap<>(ids.size());

        for (long id: ids){
            if (ingredientRepository.existsById(id)){
                Ingredient ingredient = ingredientRepository.findById(id).get();
                prices.put(id, new Tuple(ingredient.getPrice(), ingredient.getName()));
            } else {
                throw new IngredientNotFoundException("The Ingredient with id " + id + " was not found in the database");
            }
        }
        return prices;
    }

    public void checkForIngredientsExistence(List<Long> ids) throws IngredientNotFoundException {
        for (long id: ids){
            if (!ingredientRepository.existsById(id)){
                throw new IngredientNotFoundException();
            }
        }
    }

    /**
     * @return a list of all the extra ingredients of which the customers can choose to add to their pizza.
     */
    public List<Ingredient> getToppingsList(){
        List<Ingredient> ingredientList = ingredientRepository.findAll();
        return ingredientList;
    }


    /**
     * @param ingredient Ingredient instance that we want to validate.
     * @return true iff the given ingredient is valid.
     */
    public boolean validateIngredient(Ingredient ingredient){
        return ingredient != null && ingredient.getName() != null && ingredient.getName().length() > 0
                && ingredient.getPrice() >= 0.0 && ingredient.getAllergens() != null;
    }

}
