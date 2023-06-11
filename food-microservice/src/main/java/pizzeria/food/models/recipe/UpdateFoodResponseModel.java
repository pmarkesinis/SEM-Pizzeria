package pizzeria.food.models.recipe;

import lombok.Data;
import pizzeria.food.domain.recipe.Recipe;

@Data
public class UpdateFoodResponseModel {
    private long id;
    private Recipe recipe;
}
