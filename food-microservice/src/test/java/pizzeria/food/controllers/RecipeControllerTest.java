package pizzeria.food.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pizzeria.food.domain.ingredient.Ingredient;
import pizzeria.food.domain.ingredient.IngredientNotFoundException;
import pizzeria.food.domain.recipe.*;
import pizzeria.food.models.ingredient.GetBaseToppingsRequestModel;
import pizzeria.food.models.ingredient.GetBaseToppingsResponseModel;
import pizzeria.food.models.recipe.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test"})
class RecipeControllerTest {
    private transient RecipeController recipeController;
    private transient RecipeService recipeService;
    private transient RecipeServiceResponseInformation recipeServiceResponseInformation;

    @BeforeEach
    void setUp(){
        recipeService = Mockito.mock(RecipeService.class);
        recipeServiceResponseInformation = Mockito.mock(RecipeServiceResponseInformation.class);
        recipeController = new RecipeController(recipeService, recipeServiceResponseInformation);
    }
    @Test
    void saveFood() {
        Recipe recipe = new Recipe("Test", List.of(1L, 55L, 3L), 12.0);
        Recipe returned = new Recipe("Test", List.of(1L, 55L, 3L), 12.0);
        returned.setId(1L);
        try {
            when(recipeService.registerFood(recipe)).thenReturn(returned);
            SaveFoodRequestModel requestModel = new SaveFoodRequestModel();
            requestModel.setRecipe(recipe);
            ResponseEntity<SaveFoodResponseModel> response = recipeController.saveFood(requestModel);
            assertEquals(response.getBody().getRecipe(), returned);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testSaveFoodReturnsException(){
        Recipe recipe = new Recipe("Test", List.of(1L, 55L, 3L), 12.0);
        try {
            when(recipeService.registerFood(recipe)).thenThrow(RecipeAlreadyInUseException.class);
            SaveFoodRequestModel requestModel = new SaveFoodRequestModel();
            requestModel.setRecipe(recipe);
            ResponseEntity response = recipeController.saveFood(requestModel);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getHeaders().containsKey(HttpHeaders.WARNING));
        } catch (IngredientNotFoundException e) {
            fail();
        } catch (RecipeAlreadyInUseException e) {
            fail();
        } catch (InvalidRecipeException e) {
            fail();
        }
    }

    @Test
    void testSaveFoodReturnsException2(){
        Recipe recipe = new Recipe("Test", List.of(1L, 55L, 3L), 12.0);
        try {
            when(recipeService.registerFood(recipe)).thenThrow(IngredientNotFoundException.class);
            SaveFoodRequestModel requestModel = new SaveFoodRequestModel();
            requestModel.setRecipe(recipe);
            ResponseEntity response = recipeController.saveFood(requestModel);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getHeaders().containsKey(HttpHeaders.WARNING));
        } catch (IngredientNotFoundException e) {
            fail();
        } catch (RecipeAlreadyInUseException e) {
            fail();
        } catch (InvalidRecipeException e) {
            fail();
        }
    }

    @Test
    void testSaveFoodHandlingInvalidRecipeException(){
        Recipe recipe = new Recipe("Test", List.of(1L, 55L, 3L), 12.0);
        try {
            when(recipeService.registerFood(recipe)).thenThrow(InvalidRecipeException.class);
            SaveFoodRequestModel requestModel = new SaveFoodRequestModel();
            requestModel.setRecipe(recipe);
            ResponseEntity response = recipeController.saveFood(requestModel);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getHeaders().containsKey(HttpHeaders.WARNING));
        } catch (IngredientNotFoundException e) {
            fail();
        } catch (RecipeAlreadyInUseException e) {
            fail();
        } catch (InvalidRecipeException e) {
            fail();
        }
    }

    @Test
    void updateFood() {
        Recipe recipe = new Recipe(null, List.of(1L, 55L, 3L), 12.0);
        try {
            when(recipeService.updateFood(recipe, 1L)).thenReturn(recipe);
            UpdateFoodRequestModel requestModel = new UpdateFoodRequestModel();
            requestModel.setRecipe(recipe);
            requestModel.setId(1L);
            ResponseEntity<UpdateFoodResponseModel> response = recipeController.updateFood(requestModel);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getRecipe()).isEqualTo(recipe);
        } catch (IngredientNotFoundException e) {
            fail();
        } catch (RecipeNotFoundException e) {
            fail();
        } catch (InvalidRecipeException e) {
            fail();
        }
    }

    @Test
    void updateFoodThrowsException(){
        Recipe recipe = new Recipe("Test", List.of(1L, 55L, 3L), 12.0);
        try {
            when(recipeService.updateFood(recipe, 1L)).thenThrow(IngredientNotFoundException.class);
            UpdateFoodRequestModel requestModel = new UpdateFoodRequestModel();
            requestModel.setRecipe(recipe);
            requestModel.setId(1L);
            ResponseEntity response = recipeController.updateFood(requestModel);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getHeaders().containsKey(HttpHeaders.WARNING));
        } catch (IngredientNotFoundException e) {
            fail();
        } catch (RecipeNotFoundException e) {
            fail();
        } catch (InvalidRecipeException e) {
            fail();
        }
    }

    @Test
    void updateFoodThrowsException2(){
        Recipe recipe = new Recipe("Test", List.of(1L, 55L, 3L), 12.0);
        try {
            when(recipeService.updateFood(recipe, 1L)).thenThrow(RecipeNotFoundException.class);
            UpdateFoodRequestModel requestModel = new UpdateFoodRequestModel();
            requestModel.setRecipe(recipe);
            requestModel.setId(1L);
            ResponseEntity response = recipeController.updateFood(requestModel);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getHeaders().containsKey(HttpHeaders.WARNING));
        } catch (IngredientNotFoundException e) {
            fail();
        } catch (RecipeNotFoundException e) {
            fail();
        } catch (InvalidRecipeException e) {
            fail();
        }
    }

    @Test
    void updateFoodThrowsException3(){
        Recipe recipe = new Recipe("Test", List.of(1L, 55L, 3L), 12.0);
        try {
            when(recipeService.updateFood(recipe, 1L)).thenThrow(InvalidRecipeException.class);
            UpdateFoodRequestModel requestModel = new UpdateFoodRequestModel();
            requestModel.setRecipe(recipe);
            requestModel.setId(1L);
            ResponseEntity response = recipeController.updateFood(requestModel);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getHeaders().containsKey(HttpHeaders.WARNING));
        } catch (IngredientNotFoundException e) {
            fail();
        } catch (RecipeNotFoundException e) {
            fail();
        } catch (InvalidRecipeException e) {
            fail();
        }
    }

    @Test
    void deleteFood() {
        try {
            when(recipeService.deleteFood(1L)).thenReturn(true);
            DeleteFoodRequestModel requestModel = new DeleteFoodRequestModel();
            requestModel.setId(1L);
            ResponseEntity response = recipeController.deleteFood(requestModel);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        } catch (RecipeNotFoundException e) {
            fail();
        }
    }

    @Test
    void testDeleteFoodThrowsException(){
        try {
            when(recipeService.deleteFood(1L)).thenThrow(RecipeNotFoundException.class);
            DeleteFoodRequestModel requestModel = new DeleteFoodRequestModel();
            requestModel.setId(1L);
            ResponseEntity response = recipeController.deleteFood(requestModel);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getHeaders().containsKey(HttpHeaders.WARNING));
        } catch (RecipeNotFoundException e) {
            fail();
        }
    }
    @Test
    void getMenu() {
        Recipe recipe = new Recipe("Test", List.of(1L, 55L, 3L), 12.0);
        recipe.setId(1L);
        Recipe recipe2 = new Recipe("Test2", List.of(1L, 55L, 3L), 12.0);
        recipe2.setId(2L);
        when(recipeServiceResponseInformation.getMenu()).thenReturn(List.of(recipe, recipe2));
        ResponseEntity<MenuResponseModel> response = recipeController.getMenu();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getMenu()).isEqualTo(List.of(recipe, recipe2));

    }

    @Test
    void getBaseToppings(){
        long id = 1L;
        GetBaseToppingsRequestModel requestModel = new GetBaseToppingsRequestModel();
        requestModel.setRecipeId(id);
        Ingredient ingredient1 = new Ingredient("ing1", 12.00);
        Ingredient ingredient2 = new Ingredient("ing2", 6.00, List.of("vegan"));
        List<Ingredient> ingredients = List.of(ingredient1, ingredient2);
        try {
            when(recipeServiceResponseInformation.getBaseToppings(id)).thenReturn(ingredients);
            ResponseEntity<GetBaseToppingsResponseModel> response = recipeController.getBaseToppings(requestModel);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().getBaseToppings()).isEqualTo(ingredients);
        } catch (RecipeNotFoundException e) {
            fail();
        } catch (IngredientNotFoundException e) {
            fail();
        }
    }

    @Test
    void testThrowsException(){
        try {
            when(recipeServiceResponseInformation.getBaseToppings(1L)).thenThrow(RecipeNotFoundException.class);
            GetBaseToppingsRequestModel requestModel = new GetBaseToppingsRequestModel();
            requestModel.setRecipeId(1L);
            ResponseEntity response = recipeController.getBaseToppings(requestModel);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getHeaders().containsKey(HttpHeaders.WARNING));
        } catch (RecipeNotFoundException e) {
            fail();
        } catch (IngredientNotFoundException e) {
            fail();
        }

    }

    @Test
    void testThrowsException2(){
        try {
            when(recipeServiceResponseInformation.getBaseToppings(1L)).thenThrow(IngredientNotFoundException.class);
            GetBaseToppingsRequestModel requestModel = new GetBaseToppingsRequestModel();
            requestModel.setRecipeId(1L);
            ResponseEntity response = recipeController.getBaseToppings(requestModel);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getHeaders().containsKey(HttpHeaders.WARNING));
        } catch (RecipeNotFoundException e) {
            fail();
        } catch (IngredientNotFoundException e) {
            fail();
        }
    }
}