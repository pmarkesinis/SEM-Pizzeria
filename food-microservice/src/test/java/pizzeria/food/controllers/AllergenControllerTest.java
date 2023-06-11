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
import pizzeria.food.communication.HttpRequestService;
import pizzeria.food.domain.Allergens.AllergenService;
import pizzeria.food.domain.ingredient.IngredientNotFoundException;
import pizzeria.food.domain.recipe.Recipe;
import pizzeria.food.domain.recipe.RecipeNotFoundException;
import pizzeria.food.models.allergens.CheckIfRecipeIsSafeRequestModel;
import pizzeria.food.models.allergens.FilterMenuRequestModel;
import pizzeria.food.models.allergens.FilterMenuResponseModel;


import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test"})
class AllergenControllerTest {
    private transient AllergenController allergenController;
    private transient AllergenService allergenService;
    private transient HttpRequestService requestService;

    @BeforeEach
    void setUp(){
        allergenService = Mockito.mock(AllergenService.class);
        requestService = Mockito.mock(HttpRequestService.class);
        allergenController = new AllergenController(allergenService, requestService);
    }

    @Test
    void filterMenu() {
        List<String> allergens = List.of("gluten", "lactose");
        Recipe recipe = new Recipe("Test", List.of(1L, 55L), 12.0);
        Recipe recipe1 = new Recipe("Test1", List.of(1L, 55L), 13.0);
        List<Recipe> result = List.of(recipe, recipe1);
        try {
            when(requestService.getUserAllergens("a")).thenReturn(Optional.of(allergens));
            when(allergenService.filterMenu("a")).thenReturn(new FilterMenuResponseModel(result));
            when(allergenService.filterMenu("a")).thenReturn(new FilterMenuResponseModel(result));
            FilterMenuRequestModel requestModel = new FilterMenuRequestModel();
            requestModel.setAllergens(allergens);
            assertThat(allergenController.filterMenu("a").getBody().getRecipes()).isEqualTo(result);
        } catch (IngredientNotFoundException e) {
            fail();
        }
    }

    @Test
    void testFilterMenuThrowsException(){
        List<String> allergens = List.of("gluten", "lactose");
        try {
            when(requestService.getUserAllergens("a")).thenReturn(Optional.of(allergens));
            when(allergenService.filterMenu("a")).thenThrow(new IngredientNotFoundException("Test"));
            ResponseEntity<FilterMenuResponseModel> response = allergenController.filterMenu("a");
            assertEquals(response.getStatusCode(), HttpStatus.BAD_REQUEST);
            assertTrue(response.getHeaders().containsKey(HttpHeaders.WARNING));
        } catch (IngredientNotFoundException e) {
            fail();
        }

    }

    @Test
    void testBadInteractionWithUser(){
        when(requestService.getUserAllergens("a")).thenReturn(Optional.empty());
        ResponseEntity<FilterMenuResponseModel> response = allergenController.filterMenu("a");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

    }


    @Test
    void testCheckIfSafe() throws Exception{
        List<String> allergens = List.of("gluten", "lactose");
        try {
            when(requestService.getUserAllergens("a")).thenReturn(Optional.of(allergens));
            CheckIfRecipeIsSafeRequestModel requestModel = new CheckIfRecipeIsSafeRequestModel();
            requestModel.setId(1L);
            when(allergenService.checkSafety("a", requestModel)).thenReturn(Optional.of(true));
            assertThat(allergenController.checkIfSafe("a", requestModel).getBody()).isTrue();
        } catch (IngredientNotFoundException e) {
            fail();
        } catch (RecipeNotFoundException e) {
            fail();
        }
    }

    @Test
    void testCheckIfSafe2() throws Exception{
        List<String> allergens = List.of("gluten", "lactose");
        try {
            when(requestService.getUserAllergens("a")).thenReturn(Optional.of(allergens));
            CheckIfRecipeIsSafeRequestModel requestModel = new CheckIfRecipeIsSafeRequestModel();
            requestModel.setId(1L);
            when(allergenService.checkSafety("a", requestModel)).thenReturn(Optional.of(false));
            assertThat(allergenController.checkIfSafe("a", requestModel).getBody()).isFalse();
        } catch (IngredientNotFoundException e) {
            fail();
        } catch (RecipeNotFoundException e) {
            fail();
        }
    }

    @Test
    void testCheckIfSafe3() {
        when(requestService.getUserAllergens("a")).thenReturn(Optional.empty());
        CheckIfRecipeIsSafeRequestModel requestModel = new CheckIfRecipeIsSafeRequestModel();
        requestModel.setId(1L);
        ResponseEntity<Boolean> response = allergenController.checkIfSafe("a", requestModel);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void testCheckIfSafe4() throws Exception{
        List<String> allergens = List.of("gluten", "lactose");
        when(requestService.getUserAllergens("a")).thenReturn(Optional.of(allergens));
        try {
            when(allergenService.checkSafety(eq("a"), any())).thenThrow(new RecipeNotFoundException("Test"));
            CheckIfRecipeIsSafeRequestModel requestModel = new CheckIfRecipeIsSafeRequestModel();
            requestModel.setId(1L);
            ResponseEntity<Boolean> response = allergenController.checkIfSafe("a", requestModel);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertTrue(response.getHeaders().containsKey(HttpHeaders.WARNING));
        } catch (RecipeNotFoundException e) {
            fail();
        } catch (IngredientNotFoundException e) {
            fail();
        }
    }

    @Test
    void testCheckIfSafe5() throws Exception{
        List<String> allergens = List.of("gluten", "lactose");
        when(requestService.getUserAllergens("a")).thenReturn(Optional.of(allergens));
        try {
            when(allergenService.checkSafety(eq("a"), any())).thenThrow(new IngredientNotFoundException("Test"));
            CheckIfRecipeIsSafeRequestModel requestModel = new CheckIfRecipeIsSafeRequestModel();
            requestModel.setId(1L);
            ResponseEntity<Boolean> response = allergenController.checkIfSafe("a", requestModel);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertTrue(response.getHeaders().containsKey(HttpHeaders.WARNING));
        } catch (RecipeNotFoundException e) {
            fail();
        } catch (IngredientNotFoundException e) {
            fail();
        }
    }
}