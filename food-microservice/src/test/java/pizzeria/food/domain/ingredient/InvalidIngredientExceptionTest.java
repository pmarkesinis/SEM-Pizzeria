package pizzeria.food.domain.ingredient;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidIngredientExceptionTest {

    @Test
    void testConstructor(){
        InvalidIngredientException invalidIngredientException = new InvalidIngredientException();
        assertNotNull(invalidIngredientException);
        assertEquals("The ingredient's properties are invalid", invalidIngredientException.getMessage());
    }
    @Test
    void getMessage() {
        InvalidIngredientException invalidIngredientException = new InvalidIngredientException();
        assertEquals(invalidIngredientException.getMessage(), "The ingredient's properties are invalid");
    }

    @Test
    void checkThrows(){
        try {
            throw new InvalidIngredientException();
        } catch (InvalidIngredientException e) {
            assertEquals("The ingredient's properties are invalid", e.getMessage());
        }
    }
}