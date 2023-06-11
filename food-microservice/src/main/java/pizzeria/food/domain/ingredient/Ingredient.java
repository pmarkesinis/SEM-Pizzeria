package pizzeria.food.domain.ingredient;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@NoArgsConstructor
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    private long id;

    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private double price;
    @ElementCollection(fetch = FetchType.EAGER)
    @Getter
    @Setter
    private List<String> allergens;


    /**
     * @param name String value representing the name of this commons.Ingredient
     * @param price Double value representing the price of this commons.Ingredient
     * @param allergens List of Strings representing the allergies this commons.Ingredient contains
     * returns a new commons.Ingredient with the values specified above
     */
    public Ingredient(String name, double price, List<String> allergens) {
        this.name = name;
        this.price = price;
        this.allergens = allergens;
    }

    /**
     * @param name String value representing the name of this commons.Ingredient
     * @param price Double value representing the price of this commons.Ingredient
     * returns a new commons.Ingredient that contains no allergens
     */
    public Ingredient(String name, double price) {
        this.name = name;
        this.price = price;
        this.allergens = new ArrayList<>();
    }

    /**
     * @param id long value representing the new id of this ingredient
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @param o object we want to check for equality with
     * @return true iff o is an instance of ingredient and has the same id as this Ingredient
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Ingredient)) return false;
        Ingredient that = (Ingredient) o;
        return id == that.id;
    }

    /**
     * @return integer representation of this Ingredient
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

