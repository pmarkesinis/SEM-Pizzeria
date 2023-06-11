package pizzeria.food.domain.ingredient;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    // This method will be used to check if an ingredient is already in the database
    boolean existsByName(String name);


}
