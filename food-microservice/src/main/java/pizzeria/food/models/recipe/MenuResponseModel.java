package pizzeria.food.models.recipe;

import lombok.Data;
import pizzeria.food.domain.recipe.Recipe;

import java.util.List;

@Data
public class MenuResponseModel {
    private List<Recipe> menu;
}
