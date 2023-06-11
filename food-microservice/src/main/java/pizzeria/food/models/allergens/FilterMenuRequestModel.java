package pizzeria.food.models.allergens;

import lombok.Data;

import java.util.List;

@Data
public class FilterMenuRequestModel {
    private List<String> allergens;
}
