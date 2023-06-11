package pizzeria.food.models.ingredient;

import lombok.Data;
import pizzeria.food.domain.ingredient.Ingredient;

import java.util.List;

@Data
public class ExtraToppingsResponseModel {
    private List<Ingredient> ingredients;
}
