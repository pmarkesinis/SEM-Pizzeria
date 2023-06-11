package pizzeria.food.domain.ingredient;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IngredientNotFoundExceptionTest {

    @Test
    void testConstructor(){
        IngredientNotFoundException ingredientNotFoundException = new IngredientNotFoundException();
        assertNotNull(ingredientNotFoundException);
        assertEquals("The ingredient was not found in the database", ingredientNotFoundException.getMessage());
    }

    @Test
    void testConstructor2(){
        IngredientNotFoundException ingredientNotFoundException = new IngredientNotFoundException("test");
        assertNotNull(ingredientNotFoundException);
        assertEquals("test", ingredientNotFoundException.getMessage());
    }

    @Test
    void getMessage() {
        IngredientNotFoundException ingredientNotFoundException = new IngredientNotFoundException();
        assertEquals("The ingredient was not found in the database", ingredientNotFoundException.getMessage());
    }

    @Test
    void checkThrows(){
        try {
            throw new IngredientNotFoundException();
        } catch (IngredientNotFoundException e) {
            assertEquals("The ingredient was not found in the database", e.getMessage());
        }
    }
}