package pizzeria.food.models.prices;

import lombok.Data;

import java.util.Map;


@Data
public class GetPricesResponseModel {
    private Map<Long, Tuple> foodPrices;
    private Map<Long, Tuple> ingredientPrices;
}
