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
import pizzeria.food.domain.ingredient.*;
import pizzeria.food.models.ingredient.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


/*
 * All the catch clauses will never be reached because the service will always catch the exception
 * They are necessary for the compilation of the code
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test"})
class IngredientControllerTest {
    private transient IngredientController ingredientController;
    private transient IngredientService ingredientService;

    @BeforeEach
    void setUp(){
        ingredientService = Mockito.mock(IngredientService.class);
        ingredientController = new IngredientController(ingredientService);
    }
    @Test
    void saveIngredient() {
        Ingredient ingredient = new Ingredient("Test", 12.0, List.of("gluten", "lactose"));
        Ingredient ing = new Ingredient("Test", 12.0, List.of("gluten", "lactose"));
        ing.setId(1L);
        try {
            when(ingredientService.registerIngredient(ingredient)).thenReturn(ing);
            SaveIngredientRequestModel requestModel = new SaveIngredientRequestModel();
            requestModel.setIngredient(ingredient);
            ResponseEntity<SaveIngredientResponseModel> response = ingredientController.saveIngredient(requestModel);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody().getIngredient()).isEqualTo(ing);
        } catch (IngredientAlreadyInUseException | InvalidIngredientException e) {
            fail();
        }
    }

    @Test
    void saveIngredientThrowsException(){
        Ingredient ingredient = new Ingredient("Test", 12.0, List.of("gluten", "lactose"));
        try {
            when(ingredientService.registerIngredient(ingredient)).thenThrow(IngredientAlreadyInUseException.class);
            SaveIngredientRequestModel requestModel = new SaveIngredientRequestModel();
            requestModel.setIngredient(ingredient);
            ResponseEntity<SaveIngredientResponseModel> responseEntity = ingredientController.saveIngredient(requestModel);
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertTrue(responseEntity.getHeaders().containsKey(HttpHeaders.WARNING));
        } catch (IngredientAlreadyInUseException | InvalidIngredientException e) {
            fail();
        }
    }

    @Test
    void saveIngredientThrowsInvalidIngredientException(){
        Ingredient ingredient = new Ingredient("Test", 12.0, List.of("gluten", "lactose"));
        try {
            when(ingredientService.registerIngredient(ingredient)).thenThrow(InvalidIngredientException.class);
            SaveIngredientRequestModel requestModel = new SaveIngredientRequestModel();
            requestModel.setIngredient(ingredient);
            ResponseEntity<SaveIngredientResponseModel> responseEntity = ingredientController.saveIngredient(requestModel);
            assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertTrue(responseEntity.getHeaders().containsKey(HttpHeaders.WARNING));
        } catch (IngredientAlreadyInUseException | InvalidIngredientException e) {
            fail();
        }
    }

    @Test
    void updateIngredient() {
        Ingredient ingredient = new Ingredient("Test", 12.0, List.of("gluten", "lactose"));
        Ingredient returnIngredient = new Ingredient("Test", 12.0, List.of("gluten", "lactose"));
        returnIngredient.setId(1L);
        try {
            when(ingredientService.updateIngredient(ingredient, 1L)).thenReturn(returnIngredient);
            UpdateIngredientRequestModel requestModel = new UpdateIngredientRequestModel();
            requestModel.setIngredient(ingredient);
            requestModel.setId(1L);
            ResponseEntity<UpdateIngredientResponseModel> response = ingredientController.updateIngredient(requestModel);
            assertThat(response.getBody().getIngredient()).isEqualTo(returnIngredient);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        } catch (IngredientNotFoundException | InvalidIngredientException e) {
            fail();
        }
    }

    @Test
    void testUpdateIngredientContainsException(){
        Ingredient ingredient = new Ingredient("Test", 12.0, List.of("gluten", "lactose"));
        ingredient.setId(1L);
        try {
            when(ingredientService.updateIngredient(ingredient, 1L)).thenThrow(IngredientNotFoundException.class);
            UpdateIngredientRequestModel requestModel = new UpdateIngredientRequestModel();
            requestModel.setIngredient(ingredient);
            requestModel.setId(1L);
            ResponseEntity<UpdateIngredientResponseModel> response = ingredientController.updateIngredient(requestModel);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertTrue(response.getHeaders().containsKey(HttpHeaders.WARNING));
        } catch (IngredientNotFoundException | InvalidIngredientException e) {
            fail();
        }
    }

    @Test
    void testUpdateIngredientThrowsInvalidIngredientException(){
        Ingredient ingredient = new Ingredient("Test", 12.0, List.of("gluten", "lactose"));
        ingredient.setId(1L);
        try {
            when(ingredientService.updateIngredient(ingredient, 1L)).thenThrow(InvalidIngredientException.class);
            UpdateIngredientRequestModel requestModel = new UpdateIngredientRequestModel();
            requestModel.setIngredient(ingredient);
            requestModel.setId(1L);
            ResponseEntity<UpdateIngredientResponseModel> response = ingredientController.updateIngredient(requestModel);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertTrue(response.getHeaders().containsKey(HttpHeaders.WARNING));
        } catch (IngredientNotFoundException | InvalidIngredientException e) {
            fail();
        }
    }

    @Test
    void deleteIngredient() {
        long id = 1L;
        try {
            when(ingredientService.deleteIngredient(id)).thenReturn(true);
            DeleteIngredientRequestModel deleteIngredientRequestModel = new DeleteIngredientRequestModel();
            deleteIngredientRequestModel.setId(id);
            ResponseEntity response = ingredientController.deleteIngredient(deleteIngredientRequestModel);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        } catch (IngredientNotFoundException e) {
            fail();
        }
    }

    @Test
    void deleteIngredientThrowsException(){
        long id = 55L;
        try {
            when(ingredientService.deleteIngredient(id)).thenThrow(IngredientNotFoundException.class);
            DeleteIngredientRequestModel deleteIngredientRequestModel = new DeleteIngredientRequestModel();
            deleteIngredientRequestModel.setId(id);
            ResponseEntity response = ingredientController.deleteIngredient(deleteIngredientRequestModel);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertTrue(response.getHeaders().containsKey(HttpHeaders.WARNING));
        } catch (IngredientNotFoundException e) {
            fail();
        }

    }

    @Test
    void getExtraToppingsSet() {
        Ingredient ingredient1 = new Ingredient("Test1", 12.0, List.of("gluten", "lactose"));
        Ingredient ingredient2 = new Ingredient("Test2", 5.0, List.of("gluten", "lactose", "egg"));
        Ingredient ingredient3 = new Ingredient("Test3", 7.0, List.of("egg"));
        Ingredient ingredient4 = new Ingredient("Test4", 3.0, List.of("gluten", "egg"));

        List<Ingredient> extraToppingsSet = List.of(ingredient1, ingredient2, ingredient3, ingredient4);
        when(ingredientService.getToppingsList()).thenReturn(extraToppingsSet);
        ResponseEntity<ExtraToppingsResponseModel> response = ingredientController.getExtraToppingsSet();
        assertThat(response.getBody().getIngredients()).containsExactlyElementsOf(extraToppingsSet);

    }
}