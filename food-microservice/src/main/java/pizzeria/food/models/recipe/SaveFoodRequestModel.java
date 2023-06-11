package pizzeria.food.models.recipe;

import lombok.Data;
import pizzeria.food.domain.recipe.Recipe;

@Data
public class SaveFoodRequestModel {
    private Recipe recipe;
}
