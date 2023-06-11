package pizzeria.order.domain.food;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name="food")
@NoArgsConstructor
public class Food {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @NotNull
    @Column(name="id")
    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    private long recipeId;

    @ElementCollection
    @Column(name = "baseIngredients")
    @LazyCollection(LazyCollectionOption.FALSE)
    @Getter
    @Setter
    private List<Long> baseIngredients;

    @ElementCollection
    @LazyCollection(LazyCollectionOption.FALSE)
    @Column(name = "extraIngredients")
    @Getter
    @Setter
    private List<Long> extraIngredients;

    //private enum foodType {PIZZA};

    /**
     * Food constructor for testing purposes
     *
     * @param id the food id
     * @param recipeId the id of the recipe this food is based on
     * @param orderId the id of the order this food belongs to
     * @param base the list of base ingredient ids
     * @param extra the list of extra ingredient ids
     */
    public Food(long id, long recipeId, long orderId, List<Long> base, List<Long> extra){
        this.id = id;
        this.recipeId = recipeId;
        this.baseIngredients = base;
        this.extraIngredients = extra;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Food)) return false;
        Food food = (Food) o;
        return id == food.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
