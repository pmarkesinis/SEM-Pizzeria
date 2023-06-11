package pizzeria.food.models.recipe;

import lombok.Data;
import pizzeria.food.domain.recipe.Recipe;

@Data
public class UpdateFoodRequestModel {
    private long id;
    private Recipe recipe;
}
