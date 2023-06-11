package pizzeria.food.domain.recipe;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecipeAlreadyInUseExceptionTest {
    @Test
    void testConstructor(){
        RecipeAlreadyInUseException recipeAlreadyInUseException = new RecipeAlreadyInUseException();
        assertNotNull(recipeAlreadyInUseException);
        assertEquals("The recipe is already stored in the database", recipeAlreadyInUseException.getMessage());
    }

    @Test
    void testConstructor2(){
        RecipeAlreadyInUseException recipeAlreadyInUseException = new RecipeAlreadyInUseException("test");
        assertNotNull(recipeAlreadyInUseException);
        assertEquals("test", recipeAlreadyInUseException.getMessage());
    }
    @Test
    void getMessage() {
        RecipeAlreadyInUseException recipeAlreadyInUseException = new RecipeAlreadyInUseException();
        assertEquals(recipeAlreadyInUseException.getMessage(), "The recipe is already stored in the database");
    }

    @Test
    void checkThrows(){
        try {
            throw new RecipeAlreadyInUseException();
        } catch (RecipeAlreadyInUseException e) {
            assertEquals("The recipe is already stored in the database", e.getMessage());
        }
    }
}