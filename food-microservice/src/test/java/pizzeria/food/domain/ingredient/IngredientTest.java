package pizzeria.food.domain.ingredient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class IngredientTest {

    Ingredient ingredient;

    @BeforeEach
    void setUp() {
        ingredient = new Ingredient("Tomato", 1.0);
    }

    @Test
    void testConstructor1(){
        assertNotNull(ingredient);
        assertThat(ingredient.getAllergens().size()).isEqualTo(0);
    }

    @Test
    void testConstructor2(){
        ingredient = new Ingredient("Tomato", 1.0, List.of("Gluten", "Lactose"));
        assertNotNull(ingredient);
        assertThat(ingredient.getAllergens()).containsExactlyElementsOf(List.of("Gluten", "Lactose"));
    }

    @Test
    void setId() {
        ingredient.setId(1);
        assertThat(ingredient.getId()).isEqualTo(1);
    }

    @Test
    void testEquals() {
        ingredient.setId(1);
        Ingredient ingredient2 = new Ingredient("Tomato", 1.0);
        ingredient2.setId(1);
        assertEquals(ingredient, ingredient2);
    }

    @Test
    void testEquals2(){
        ingredient.setId(1);
        assertEquals(ingredient, ingredient);
    }

    @Test
    void testEquals3(){
        assertNotEquals(ingredient, null);
    }
    @Test
    void testEquals4(){
        Ingredient ingredient2 = new Ingredient("Tomato", 1.0);
        ingredient2.setId(2);
        ingredient.setId(1);
        assertNotEquals(ingredient, ingredient2);
    }

    @Test
    void testHashCode() {
        ingredient.setId(1);
        Ingredient ingredient2 = new Ingredient("Tomato", 1.0);
        ingredient2.setId(1);
        assertEquals(ingredient.hashCode(), ingredient2.hashCode());
    }

    @Test
    void testHashCode2(){
        ingredient.setId(1);
        assertEquals(ingredient.hashCode(), ingredient.hashCode());
    }

    @Test
    void testHashCode3(){
        Ingredient ingredient2 = new Ingredient("Tomato", 1.0);
        ingredient2.setId(2);
        ingredient.setId(1);
        assertNotEquals(ingredient.hashCode(), ingredient2.hashCode());
    }

    @Test
    void getId() {
        assertEquals(ingredient.getId(), 0);
    }

    @Test
    void getName() {
        assertEquals(ingredient.getName(), "Tomato");
    }

    @Test
    void getPrice() {
        assertEquals(ingredient.getPrice(), 1.0);
    }

    @Test
    void getAllergens() {
        Ingredient ing = new Ingredient("Tomato", 1.0, List.of("Gluten", "Lactose"));
        assertThat(ing.getAllergens()).containsExactlyElementsOf(List.of("Gluten", "Lactose"));
    }

    @Test
    void setName() {
        ingredient.setName("Sauce");
        assertEquals(ingredient.getName(), "Sauce");
    }

    @Test
    void setPrice() {
        ingredient.setPrice(2.0);
        assertEquals(ingredient.getPrice(), 2.0);
    }

    @Test
    void setAllergens(){
        ingredient.setAllergens(List.of("Gluten", "Lactose"));
        assertThat(ingredient.getAllergens()).containsExactlyElementsOf(List.of("Gluten", "Lactose"));
    }
}