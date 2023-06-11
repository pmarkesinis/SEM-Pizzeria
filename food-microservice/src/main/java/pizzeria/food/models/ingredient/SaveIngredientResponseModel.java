package pizzeria.food.models.ingredient;

import lombok.Data;
import pizzeria.food.domain.ingredient.Ingredient;

@Data
public class SaveIngredientResponseModel {
    private long id;
    private Ingredient ingredient;
}
