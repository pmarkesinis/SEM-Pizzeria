package pizzeria.order.models;

import lombok.Data;
import pizzeria.order.Application;

import java.util.List;

/**
 * The type Get prices request model
 */
@Data
public class GetPricesRequestModel {
    private List<Long> foodIds;
    private List<Long> ingredientIds;

    /**
     * Instantiates a new Get prices request model.
     *
     * @param foodIds       the food ids included in the order
     * @param ingredientIds the ingredient ids included in the order
     */
    @Application.ExcludeFromJacocoGeneratedReportConstructor
    public GetPricesRequestModel(List<Long>foodIds, List<Long>ingredientIds) {
        this.foodIds = foodIds;
        this.ingredientIds = ingredientIds;
    }

}
