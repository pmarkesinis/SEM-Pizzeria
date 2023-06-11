package pizzeria.food.domain;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pizzeria.food.domain.Allergens.AllergenService;
import pizzeria.food.domain.ingredient.Ingredient;
import pizzeria.food.domain.ingredient.IngredientNotFoundException;
import pizzeria.food.domain.ingredient.IngredientRepository;
import pizzeria.food.domain.recipe.Recipe;
import pizzeria.food.domain.recipe.RecipeNotFoundException;
import pizzeria.food.domain.recipe.RecipeRepository;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class AllergenServiceTests {
    @Autowired
    private transient IngredientRepository ingredientRepository;

    @Autowired
    private transient RecipeRepository recipeRepository;
    @Autowired
    private transient AllergenService allergenService;

    @Test
    void filterMenuOnAllergens() {

        ingredientRepository.save(new Ingredient("Ingredient1", 5.0, List.of("Al56", "Al57", "Al58")));
        ingredientRepository.save(new Ingredient("Ingredient2", 5.0, List.of("Al6", "Al5", "Al9")));
        ingredientRepository.save(new Ingredient("Ingredient3", 5.0, List.of("Al1", "Al2", "Al3")));
        ingredientRepository.save(new Ingredient("Ingredient4", 5.0, List.of("Al56", "Al44", "Al9")));

        recipeRepository.save(new Recipe("Recipe1", List.of(1L, 2L, 3L), 12.50));
        recipeRepository.save(new Recipe("Recipe2", List.of(4L, 3L), 12.50));
        recipeRepository.save(new Recipe("Recipe3", List.of(1L, 2L, 4L), 12.50));

        try {
            List<Recipe> result = allergenService.filterMenuOnAllergens(List.of());
            assertThat(result).hasSize(3);
        } catch (IngredientNotFoundException e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    void filterMenuOnAllergensWith1AllergenThatOneRecipeContains() {
        ingredientRepository.save(new Ingredient("Ingredient1", 5.0, List.of("Al56", "Al57", "Al58")));
        ingredientRepository.save(new Ingredient("Ingredient2", 5.0, List.of("Al6", "Al5", "Al9")));
        ingredientRepository.save(new Ingredient("Ingredient3", 5.0, List.of("Al1", "Al2", "Al3")));
        ingredientRepository.save(new Ingredient("Ingredient4", 5.0, List.of("Al56", "Al44", "Al9")));

        recipeRepository.save(new Recipe("Recipe1", List.of(1L, 2L, 3L), 12.50));
        recipeRepository.save(new Recipe("Recipe2", List.of(4L, 3L), 12.50));
        recipeRepository.save(new Recipe("Recipe3", List.of(2L, 4L), 12.50));
        try {
            List<Recipe> result = allergenService.filterMenuOnAllergens(List.of("Al58"));
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getName()).isEqualTo("Recipe2");
            assertThat(result.get(1).getName()).isEqualTo("Recipe3");
        } catch (IngredientNotFoundException e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    void filterMenu2(){
        ingredientRepository.save(new Ingredient("Ingredient1", 5.0, List.of("Al56", "Al57", "Al58")));
        ingredientRepository.save(new Ingredient("Ingredient2", 5.0, List.of("Al67", "Al99", "Al19")));
        ingredientRepository.save(new Ingredient("Ingredient3", 5.0, List.of("Al1", "Al56", "Al3")));
        ingredientRepository.save(new Ingredient("Ingredient4", 5.0, List.of("Al56", "Al44", "Al9")));
        ingredientRepository.save(new Ingredient("Ingredient5", 5.0, List.of("Al56", "Al44", "Al9")));
        ingredientRepository.save(new Ingredient("Ingredient6", 5.0, List.of("Al18")));

        recipeRepository.save(new Recipe("Recipe1", List.of(1L, 2L), 12.50));
        recipeRepository.save(new Recipe("Recipe2", List.of(3L, 4L), 12.50));
        recipeRepository.save(new Recipe("Recipe3", List.of(5L, 6L), 12.50));

        try {
            List<Recipe> result = allergenService.filterMenuOnAllergens(List.of("Al56", "Al44"));
            assertThat(result.size()).isEqualTo(0);
        } catch (IngredientNotFoundException e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    void filterMenu3(){
        ingredientRepository.save(new Ingredient("Ingredient1", 5.0, List.of("Al56", "Al57", "Al58")));
        ingredientRepository.save(new Ingredient("Ingredient2", 5.0, List.of("Al67", "Al99", "Al19")));
        ingredientRepository.save(new Ingredient("Ingredient3", 5.0, List.of("Al1", "Al56", "Al3")));
        ingredientRepository.save(new Ingredient("Ingredient4", 5.0, List.of("Al56", "Al44", "Al9")));
        ingredientRepository.save(new Ingredient("Ingredient5", 5.0, List.of("Al56", "Al44", "Al9")));
        ingredientRepository.save(new Ingredient("Ingredient6", 5.0, List.of("Al18")));

        recipeRepository.save(new Recipe("Recipe1", List.of(1L, 2L), 12.50));
        recipeRepository.save(new Recipe("Recipe2", List.of(3L, 4L), 12.50));
        recipeRepository.save(new Recipe("Recipe3", List.of(5L, 6L), 12.50));

        try {
            List<Recipe> result = allergenService.filterMenuOnAllergens(List.of("Al18", "Al3"));
            assertThat(result.size()).isEqualTo(1);
            assertThat(result.get(0).getName()).isEqualTo("Recipe1");
        } catch (IngredientNotFoundException e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    void testFilterMenu4(){
        ingredientRepository.save(new Ingredient("Ingredient1", 5.0, List.of("Al56", "Al57", "Al58")));
        ingredientRepository.save(new Ingredient("Ingredient2", 5.0, List.of("Al67", "Al99", "Al19")));
        ingredientRepository.save(new Ingredient("Ingredient3", 5.0, List.of("Al1", "Al56", "Al3")));
        ingredientRepository.save(new Ingredient("Ingredient4", 5.0, List.of("Al56", "Al44", "Al9")));
        ingredientRepository.save(new Ingredient("Ingredient5", 5.0, List.of("Al56", "Al44", "Al9")));
        ingredientRepository.save(new Ingredient("Ingredient6", 5.0, List.of("Al18")));

        recipeRepository.save(new Recipe("Recipe1", List.of(1L, 2L), 12.50));
        recipeRepository.save(new Recipe("Recipe2", List.of(3L, 4L), 12.50));
        recipeRepository.save(new Recipe("Recipe3", List.of(5L, 6L), 12.50));

        try {
            List<Recipe> result = allergenService.filterMenuOnAllergens(List.of("99"));
            assertThat(result.size()).isEqualTo(3);
        } catch (IngredientNotFoundException e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    void recipeIsSafe() {
        ingredientRepository.save(new Ingredient("Ingredient1", 10, List.of("Al1", "Al2")));
        ingredientRepository.save(new Ingredient("Ingredient2", 10, List.of("Al3", "Al4")));
        ingredientRepository.save(new Ingredient("Ingredient3", 10, List.of("Al7", "Al5")));

        Recipe recipe1 = new Recipe("recipe1", List.of(1L, 2L, 3L), 30.0);

        try {
            assertThat(allergenService.recipeIsSafe(recipe1, List.of("Al10", "Al11"))).isTrue();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }
    @Test
    void recipeIsNotSafe() {
        ingredientRepository.save(new Ingredient("Ingredient1", 10, List.of("Al1", "Al2")));
        ingredientRepository.save(new Ingredient("Ingredient2", 10, List.of("Al3", "Al4")));
        ingredientRepository.save(new Ingredient("Ingredient3", 10, List.of("Al7", "Al5")));

        Recipe recipe1 = new Recipe("recipe1", List.of(1L, 2L, 3L), 30.0);

        try {
            assertThat(allergenService.recipeIsSafe(recipe1, List.of("Al10", "Al7"))).isFalse();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    void ingredientNotFound() {
        ingredientRepository.save(new Ingredient("Ingredient1", 10, List.of("Al1", "Al2")));
        ingredientRepository.save(new Ingredient("Ingredient2", 10, List.of("Al3", "Al4")));
        ingredientRepository.save(new Ingredient("Ingredient3", 10, List.of("Al7", "Al5")));

        Recipe recipe1 = new Recipe("recipe1", List.of(1L, 2L, 3L, 4L), 30.0);

        assertThrows(IngredientNotFoundException.class, () -> {
            allergenService.recipeIsSafe(recipe1, List.of());
        });
    }

    @Test
    void testNoAllergens(){
        ingredientRepository.save(new Ingredient("Ingredient1", 10, List.of("Al1", "Al2")));
        ingredientRepository.save(new Ingredient("Ingredient2", 10, List.of("Al3", "Al4", "Al2")));
        ingredientRepository.save(new Ingredient("Ingredient3", 10, List.of("Al7", "Al5", "Al1")));

        Recipe recipe1 = new Recipe("recipe1", List.of(1L, 2L, 3L), 30.0);

        try {
            assertThat(allergenService.recipeIsSafe(recipe1, List.of())).isTrue();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    void testRecipeWithOneIngredientThatIsSafe(){
        ingredientRepository.save(new Ingredient("Ingredient1", 10, List.of("Al1", "Al3", "Al9", "Al12")));
        Recipe recipe = new Recipe("recipe1", List.of(1L), 12.50);

        try {
            assertThat(allergenService.recipeIsSafe(recipe, List.of("Al17", "Al19", "Al4"))).isTrue();
        } catch (IngredientNotFoundException e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    void testRecipeWithOneIngredientThatIsSafe2(){
        ingredientRepository.save(new Ingredient("Ingredient1", 10, List.of("Al1", "Al3", "Al9", "Al12", "Al17")));
        Recipe recipe = new Recipe("recipe1", List.of(1L), 12.50);
        try {
            assertThat(allergenService.recipeIsSafe(recipe, List.of("Al16", "Al19", "Al4"))).isTrue();
        } catch (IngredientNotFoundException e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    void testRecipeWithOneIngredientThatIsNotSafe(){
        ingredientRepository.save(new Ingredient("Ingredient1", 10, List.of("Al1", "Al3", "Al9", "Al12", "Al17")));
        Recipe recipe = new Recipe("recipe1", List.of(1L), 12.50);
        try {
            assertThat(allergenService.recipeIsSafe(recipe, List.of("Al16", "Al19", "Al4", "Al1"))).isFalse();
        } catch (IngredientNotFoundException e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    void testRecipeWithOneIngredientThatIsNotSafe2(){
        ingredientRepository.save(new Ingredient("Ingredient1", 10, List.of("Al1", "Al3", "Al9", "Al12", "Al17")));
        Recipe recipe = new Recipe("recipe1", List.of(1L), 12.50);
        try {
            assertThat(allergenService.recipeIsSafe(recipe, List.of("Al16", "Al19", "Al4", "Al17"))).isFalse();
        } catch (IngredientNotFoundException e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    void testRecipeWithMultipleIngredientsIsSafe(){
        // create 4 ingredients with each 2 to 5 allergens
        ingredientRepository.save(new Ingredient("Ingredient1", 10, List.of("Al1", "Al3", "Al9", "Al12", "Al17")));
        ingredientRepository.save(new Ingredient("Ingredient2", 10, List.of("Al1", "Al3")));
        ingredientRepository.save(new Ingredient("Ingredient3", 10, List.of("Al14", "Al23", "Al9", "Al12")));
        ingredientRepository.save(new Ingredient("Ingredient4", 10, List.of("Al1", "Al3", "Al9", "Al12", "Al17", "Al14", "Al23")));

        Recipe recipe = new Recipe("recipe1", List.of(1L, 2L, 3L, 4L), 12.50);
        try {
            assertThat(allergenService.recipeIsSafe(recipe, List.of())).isTrue();
        } catch (IngredientNotFoundException e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    void testRecipeWithMultipleIngredientsIsNotSafe(){
        ingredientRepository.save(new Ingredient("Ingredient1", 10, List.of("Al1", "Al3", "Al9", "Al12", "Al17")));
        ingredientRepository.save(new Ingredient("Ingredient2", 10, List.of("Al1", "Al3")));
        ingredientRepository.save(new Ingredient("Ingredient3", 10, List.of("Al14", "Al23", "Al9", "Al12")));
        ingredientRepository.save(new Ingredient("Ingredient4", 10, List.of("Al1", "Al3", "Al9", "Al12", "Al17", "Al14", "Al23")));

        Recipe recipe = new Recipe("recipe1", List.of(1L, 2L, 3L, 4L), 12.50);
        try {
            assertThat(allergenService.recipeIsSafe(recipe, List.of("Al1"))).isFalse();
        } catch (IngredientNotFoundException e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    void testIngredientsWithMultipleIngredientsIsSafe(){
        ingredientRepository.save(new Ingredient("Ingredient1", 10, List.of("Al1", "Al3", "Al9", "Al12", "Al17")));
        ingredientRepository.save(new Ingredient("Ingredient2", 10, List.of("Al1", "Al3")));
        ingredientRepository.save(new Ingredient("Ingredient3", 10, List.of("Al14", "Al23", "Al9", "Al12")));
        ingredientRepository.save(new Ingredient("Ingredient4", 10, List.of("Al1", "Al3", "Al9", "Al12", "Al17", "Al14", "Al23")));

        Recipe recipe = new Recipe("recipe1", List.of(1L, 2L, 3L, 4L), 12.50);
        try {
            assertThat(allergenService.recipeIsSafe(recipe, List.of("Al15", "Al22", "Al7"))).isTrue();
        } catch (IngredientNotFoundException e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    void testIngredientWithMultipleAllergensIsNotSafe(){
        // create 3 ingredients where 2 ingredients share allergens
        ingredientRepository.save(new Ingredient("Ingredient1", 10, List.of("Al1", "Al3", "Al9", "Al12", "Al17")));
        ingredientRepository.save(new Ingredient("Ingredient2", 10, List.of("Al1", "Al3")));
        ingredientRepository.save(new Ingredient("Ingredient3", 10, List.of("Al14", "Al23", "Al9", "Al12")));

        Recipe recipe = new Recipe("recipe1", List.of(3L, 2L, 1L), 12.50);

        try {
            assertThat(allergenService.recipeIsSafe(recipe, List.of("Al1"))).isFalse();
        } catch (IngredientNotFoundException e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    void testIngredientWithMultipleAllergensIsNotSafe2() {
        ingredientRepository.save(new Ingredient("Ingredient1", 10, List.of("Al1", "Al3", "Al9", "Al12", "Al17")));
        ingredientRepository.save(new Ingredient("Ingredient2", 10, List.of("Al1", "Al3")));
        ingredientRepository.save(new Ingredient("Ingredient3", 10, List.of("Al14", "Al23", "Al9", "Al12")));

        Recipe recipe = new Recipe("recipe1", List.of(3L, 2L, 1L), 12.50);

        try {
            assertThat(allergenService.recipeIsSafe(recipe, List.of("Al3", "Al1"))).isFalse();
        } catch (IngredientNotFoundException e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    void testRecipeIsNotSafe() {
        ingredientRepository.save(new Ingredient("Ingredient1", 10, List.of("Al1", "Al3", "Al9", "Al12", "Al17")));
        ingredientRepository.save(new Ingredient("Ingredient2", 10, List.of("Al1", "Al3")));
        ingredientRepository.save(new Ingredient("Ingredient3", 10, List.of("Al14", "Al23", "Al9", "Al12")));
        ingredientRepository.save(new Ingredient("Ingredient4", 10, List.of("Al1", "Al3", "Al9", "Al12", "Al17", "Al14", "Al23")));

        Recipe recipe = new Recipe("recipe1", List.of(1L, 2L, 3L, 4L), 12.50);
        try {
            assertThat(allergenService.recipeIsSafe(recipe, List.of("Al88", "Al77", "Al23", "Al9"))).isFalse();
        } catch (IngredientNotFoundException e) {
            System.out.println(e.getMessage());
            assertTrue(false);
        }
    }

    @Test
    void testRecipeIdIsSafe(){
        ingredientRepository.save(new Ingredient("Ingredient1", 10, List.of("Al1", "Al3", "Al9", "Al12", "Al17")));
        ingredientRepository.save(new Ingredient("Ingredient2", 10, List.of("Al1", "Al3")));
        ingredientRepository.save(new Ingredient("Ingredient3", 10, List.of("Al14", "Al23", "Al9", "Al12")));
        Recipe recipe = new Recipe("recipe1", List.of(3L, 2L, 1L), 12.50);
        recipe = recipeRepository.save(recipe);
        try {
            assertThat(allergenService.checkIfSafeRecipeWithId(recipe.getId(), List.of("Al111", "Al23"))).isFalse();

        } catch (RecipeNotFoundException | IngredientNotFoundException e) {
            fail();
        }
    }

    @Test
    void testRecipeIdIsSafe2(){
        ingredientRepository.save(new Ingredient("Ingredient1", 10, List.of("Al1", "Al3", "Al9", "Al12", "Al17")));
        ingredientRepository.save(new Ingredient("Ingredient2", 10, List.of("Al1", "Al3")));
        ingredientRepository.save(new Ingredient("Ingredient3", 10, List.of("Al14", "Al23", "Al9", "Al12")));
        Recipe recipe = new Recipe("recipe1", List.of(3L, 2L, 1L), 12.50);
        recipe = recipeRepository.save(recipe);
        try {
            assertThat(allergenService.checkIfSafeRecipeWithId(recipe.getId(), List.of("Al111", "A233", "Al465"))).isTrue();

        } catch (RecipeNotFoundException | IngredientNotFoundException e) {
            fail();
        }
    }

    @Test
    void testRecipeIdIsSafe3(){
        ingredientRepository.save(new Ingredient("Ingredient1", 10, List.of("Al1", "Al3", "Al9", "Al12", "Al17")));
        ingredientRepository.save(new Ingredient("Ingredient2", 10, List.of("Al1", "Al3")));
        ingredientRepository.save(new Ingredient("Ingredient3", 10, List.of("Al14", "Al23", "Al9", "Al12")));
        Recipe recipe = new Recipe("recipe1", List.of(3L, 2L, 1L), 12.50);
        recipe = recipeRepository.save(recipe);
        try {
            assertThat(allergenService.checkIfSafeRecipeWithId(recipe.getId(), List.of("Al3", "A23"))).isFalse();

        } catch (RecipeNotFoundException | IngredientNotFoundException e) {
            fail();
        }
    }

    @Test
    void testRecipeIdIsSafeIdNotFound(){
        assertThrows(RecipeNotFoundException.class, () -> allergenService.checkIfSafeRecipeWithId(1L, List.of("Al111", "A233", "Al465")));
    }
}
