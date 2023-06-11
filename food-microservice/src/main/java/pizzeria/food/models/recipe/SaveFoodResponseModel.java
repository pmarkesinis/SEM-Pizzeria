package pizzeria.food.models.recipe;

import lombok.Data;
import pizzeria.food.domain.recipe.Recipe;

@Data
public class SaveFoodResponseModel {
    private long id;
    private Recipe recipe;
}
