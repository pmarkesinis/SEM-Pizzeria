package pizzeria.food.domain.recipe;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RecipeNotFoundExceptionTest {
    @Test
    void testConstructor(){
        RecipeNotFoundException recipeNotFoundException = new RecipeNotFoundException();
        assertNotNull(recipeNotFoundException);
        assertEquals("The recipe could not be found in the database", recipeNotFoundException.getMessage());
    }

    @Test
    void testConstructor2(){
        RecipeNotFoundException recipeNotFoundException = new RecipeNotFoundException("test");
        assertNotNull(recipeNotFoundException);
        assertEquals("test", recipeNotFoundException.getMessage());
    }

    @Test
    void getMessage() {
        RecipeNotFoundException recipeNotFoundException = new RecipeNotFoundException();
        assertEquals(recipeNotFoundException.getMessage(), "The recipe could not be found in the database");
    }

    @Test
    void checkThrows(){
        try {
            throw new RecipeNotFoundException();
        } catch (RecipeNotFoundException e) {
            assertEquals("The recipe could not be found in the database", e.getMessage());
        }
    }
}