package pizzeria.food.domain.recipe;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pizzeria.food.domain.HasEvents;
import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@NoArgsConstructor
public class Recipe extends HasEvents {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    private String name;

    @ElementCollection(fetch = FetchType.EAGER)
    @Getter
    @Setter
    private List<Long> baseToppings;

    @Getter
    @Setter
    private double basePrice;

    @Getter
    private FoodType foodType = FoodType.PIZZA;


    /**
     * @param name String value representing the name of this Food instance
     * @param baseToppings List of ingredients representing the selected baseToppings
     * @param basePrice double value representing the price of the food without any extra toppings
     */
    public Recipe(String name, List<Long> baseToppings, double basePrice) {
        this.name = name;
        this.baseToppings = baseToppings;
        this.basePrice = basePrice;
    }

    /**
     * @param o Object to compare to
     * @return true iff o is an instance of a recipe and has the same id as this recipe
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Recipe)) return false;
        Recipe recipe = (Recipe) o;
        return getId() == recipe.getId();
    }

    /**
     * @return an integer representation of this recipe
     */
    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}



