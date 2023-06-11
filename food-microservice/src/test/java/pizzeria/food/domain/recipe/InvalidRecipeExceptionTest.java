package pizzeria.food.domain.recipe;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InvalidRecipeExceptionTest {

    @Test
    void testConstructor(){
        InvalidRecipeException exception = new InvalidRecipeException();
        assertEquals("The recipe has some invalid values", exception.getMessage());
        assertNotNull(exception);
    }


    @Test
    void getMessage() {
        InvalidRecipeException exception = new InvalidRecipeException();
        assertEquals("The recipe has some invalid values", exception.getMessage());
    }

    @Test
    void testThrows(){
        assertThrows(InvalidRecipeException.class, () -> {
            throw new InvalidRecipeException();
        });
    }
}