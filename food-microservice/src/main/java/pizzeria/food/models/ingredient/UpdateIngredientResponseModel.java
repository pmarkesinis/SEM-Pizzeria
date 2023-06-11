package pizzeria.food.models.ingredient;

import lombok.Data;
import pizzeria.food.domain.ingredient.Ingredient;

@Data
public class UpdateIngredientResponseModel {
    private long id;
    private Ingredient ingredient;
}
