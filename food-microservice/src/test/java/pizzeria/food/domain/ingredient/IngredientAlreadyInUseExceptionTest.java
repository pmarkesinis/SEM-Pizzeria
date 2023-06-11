package pizzeria.food.domain.ingredient;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IngredientAlreadyInUseExceptionTest {
    @Test
    void testConstructor(){
        IngredientAlreadyInUseException ingredientAlreadyInUseException = new IngredientAlreadyInUseException();
        assertNotNull(ingredientAlreadyInUseException);
        assertEquals("The ingredient is already stored in the database", ingredientAlreadyInUseException.getMessage());
    }

    @Test
    void testConstructor2(){
        IngredientAlreadyInUseException ingredientAlreadyInUseException = new IngredientAlreadyInUseException("test");
        assertNotNull(ingredientAlreadyInUseException);
        assertEquals("test", ingredientAlreadyInUseException.getMessage());
    }
    @Test
    void getMessage() {
        IngredientAlreadyInUseException ingredientAlreadyInUseException = new IngredientAlreadyInUseException();
        assertEquals(ingredientAlreadyInUseException.getMessage(), "The ingredient is already stored in the database");
    }

    @Test
    void checkThrows(){
        try {
            throw new IngredientAlreadyInUseException();
        } catch (IngredientAlreadyInUseException e) {
            assertEquals("The ingredient is already stored in the database", e.getMessage());
        }
    }
}