package pizzeria.food.models.allergens;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pizzeria.food.domain.recipe.Recipe;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterMenuResponseModel {
    private List<Recipe> recipes;
}
