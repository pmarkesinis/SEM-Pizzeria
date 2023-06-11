package pizzeria.order.models;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type Tuple to store price matched with a name
 * used for recipes and ingredients
 */
@Data
@NoArgsConstructor
public class Tuple {
    private double price;
    private String name;

    /**
     * Instantiates a new Tuple.
     *
     * @param price the price of the recipe/ingredient
     * @param name  the name of the recipe/ingredient
     */
    public Tuple(double price, String name) {
        this.price = price;
        this.name = name;
    }
}
