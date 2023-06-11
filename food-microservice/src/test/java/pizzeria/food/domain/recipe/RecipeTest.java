package pizzeria.food.domain.recipe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pizzeria.food.domain.ingredient.Ingredient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


class RecipeTest {

    private Recipe recipe;
    @BeforeEach
    void setUp() {
        recipe = new Recipe("Test", List.of(1L, 2L, 3L), 10.0);
    }

    @Test
    void testConstructor() {
        assertNotNull(recipe);
    }

    @Test
    void testEquals() {
        recipe.setId(1L);
        Recipe recipe2 = new Recipe("Test", List.of(1L, 2L, 3L), 10.0);
        recipe2.setId(1L);
        assertEquals(recipe, recipe2);
    }

    @Test
    void testEquals2(){
        recipe.setId(1L);
        Recipe recipe2 = new Recipe("Test", List.of(1L, 2L, 3L), 10.0);
        recipe2.setId(2L);
        assertNotEquals(recipe, recipe2);
    }

    @Test
    void testEquals3(){
        recipe.setId(1L);
        Recipe recipe2 = new Recipe("Test2", List.of(1L, 99L, 3L), 10.0);
        recipe2.setId(1L);
        assertEquals(recipe, recipe2);
    }

    @Test
    void testEquals4(){
        assertEquals(recipe, recipe);
    }

    @Test
    void testHashCode() {
        assertEquals(recipe.hashCode(), recipe.hashCode());
    }

    @Test
    void testHashCode2(){
        recipe.setId(1L);
        Recipe recipe2 = new Recipe("Test", List.of(1L, 2L, 3L), 10.0);
        recipe2.setId(1L);
        assertEquals(recipe.hashCode(), recipe2.hashCode());
    }

    @Test
    void testHashCode3(){
        recipe.setId(1L);
        Recipe recipe2 = new Recipe("Test", List.of(1L, 2L, 3L), 10.0);
        recipe2.setId(2L);
        assertNotEquals(recipe.hashCode(), recipe2.hashCode());
    }

    @Test
    void testHashCode4(){
        recipe.setId(1L);
        Recipe recipe2 = new Recipe("Test2", List.of(1L, 99L, 3L), 10.0);
        recipe2.setId(1L);
        assertEquals(recipe.hashCode(), recipe2.hashCode());
    }

    @Test
    void getId() {
        recipe.setId(1L);
        assertEquals(1L, recipe.getId());
    }

    @Test
    void getName() {
        assertEquals("Test", recipe.getName());
    }

    @Test
    void getBaseToppings() {
        assertThat(recipe.getBaseToppings()).containsExactly(1L, 2L, 3L);
    }

    @Test
    void getBasePrice() {
        assertEquals(10.0, recipe.getBasePrice());
    }

    @Test
    void getFoodType() {
        assertEquals(FoodType.PIZZA, recipe.getFoodType());
    }

    @Test
    void setName() {
        recipe.setName("Test2");
        assertEquals("Test2", recipe.getName());
    }

    @Test
    void setBaseToppings() {
        recipe.setBaseToppings(List.of(1L, 2L, 3L, 4L));
        assertThat(recipe.getBaseToppings()).containsExactly(1L, 2L, 3L, 4L);
    }

    @Test
    void setBasePrice() {
        recipe.setBasePrice(11.0);
        assertEquals(11.0, recipe.getBasePrice());
    }

    @Test
    void testEqualsIsNotInstanceOfRecipe(){
        Recipe recipe = new Recipe("Test", List.of(1L, 2L, 3L), 10.0);
        Ingredient ingredient = new Ingredient("Test", 10.0);
        recipe.setId(1L);
        ingredient.setId(1L);
        assertNotEquals(recipe, ingredient);
    }
}