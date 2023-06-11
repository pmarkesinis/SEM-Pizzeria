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
import pizzeria.food.domain.ingredient.IngredientNotFoundException;
import pizzeria.food.domain.ingredient.IngredientService;
import pizzeria.food.domain.recipe.RecipeNotFoundException;
import pizzeria.food.domain.recipe.RecipeService;
import pizzeria.food.domain.recipe.RecipeServiceResponseInformation;
import pizzeria.food.models.prices.GetPricesRequestModel;
import pizzeria.food.models.prices.GetPricesResponseModel;
import pizzeria.food.models.prices.Tuple;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test"})
class PriceControllerTest {

    private transient PriceController priceController;
    private transient RecipeService recipeService;
    private transient IngredientService ingredientService;
    private transient RecipeServiceResponseInformation recipeServiceResponseInformation;

    @BeforeEach
    void setUp(){
        ingredientService = Mockito.mock(IngredientService.class);
        recipeService = Mockito.mock(RecipeService.class);
        recipeServiceResponseInformation = Mockito.mock(RecipeServiceResponseInformation.class);
        priceController = new PriceController(recipeService, ingredientService, recipeServiceResponseInformation);
    }
    @Test
    void getPrices() {
        List<Long> recipeIds = List.of(5L, 8L, 88L);
        List<Long> ingredientIds = List.of(52L, 4L, 78L, 99L);

        Map<Long, Tuple> recipePrices = Map.of(
                5L, new Tuple(5.0, "Test"),
                8L, new Tuple(8.0, "Test1"),
                88L, new Tuple(88.0, "Test2")
        );

        Map<Long, Tuple> ingredientPrices = Map.of(
                52L, new Tuple(52.0, "Test"),
                4L, new Tuple(4.0, "Test1"),
                78L, new Tuple(78.0, "Test2"),
                99L, new Tuple(99.0, "Test3")
        );

        try {
            when(recipeServiceResponseInformation.getPrices(recipeIds)).thenReturn(recipePrices);
            when(ingredientService.getDetails(ingredientIds)).thenReturn(ingredientPrices);
            GetPricesRequestModel requestModel = new GetPricesRequestModel();
            requestModel.setFoodIds(recipeIds);
            requestModel.setIngredientIds(ingredientIds);
            GetPricesResponseModel responseModel = priceController.getPrices(requestModel).getBody();
            assertNotNull(responseModel);
            assertThat(responseModel.getFoodPrices()).isEqualTo(recipePrices);
            assertThat(responseModel.getIngredientPrices()).isEqualTo(ingredientPrices);
        } catch (RecipeNotFoundException e) {
            fail();
        } catch (IngredientNotFoundException e) {
            fail();
        }

    }

    @Test
    void testGetPricesThrowsExceptionRecipeNotFound(){
        List<Long> recipeIds = List.of(5L, 8L, 88L);
        List<Long> ingredientIds = List.of(52L, 4L, 78L, 99L);

        Map<Long, Tuple> ingredientPrices = Map.of(
                52L, new Tuple(52.0, "Test"),
                4L, new Tuple(4.0, "Test1"),
                78L, new Tuple(78.0, "Test2"),
                99L, new Tuple(99.0, "Test3")
        );

        try {
            when(recipeServiceResponseInformation.getPrices(recipeIds)).thenThrow(new RecipeNotFoundException());
            when(ingredientService.getDetails(ingredientIds)).thenReturn(ingredientPrices);
            GetPricesRequestModel requestModel = new GetPricesRequestModel();
            requestModel.setFoodIds(recipeIds);
            requestModel.setIngredientIds(ingredientIds);
            ResponseEntity<GetPricesResponseModel> response = priceController.getPrices(requestModel);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getHeaders().containsKey(HttpHeaders.WARNING));

        } catch (IngredientNotFoundException e) {
            fail();
        } catch (RecipeNotFoundException e) {
            fail();
        }

    }

    @Test
    void testGetPricesThrowsIngredientNotFoundException(){
        List<Long> recipeIds = List.of(5L, 8L, 88L);
        List<Long> ingredientIds = List.of(52L, 4L, 78L, 99L);

        Map<Long, Tuple> recipePrices = Map.of(
                5L, new Tuple(5.0, "Test"),
                8L, new Tuple(8.0, "Test1"),
                88L, new Tuple(88.0, "Test2")
        );

        try {
            when(recipeServiceResponseInformation.getPrices(recipeIds)).thenReturn(recipePrices);
            when(ingredientService.getDetails(ingredientIds)).thenThrow(new IngredientNotFoundException());
            GetPricesRequestModel requestModel = new GetPricesRequestModel();
            requestModel.setFoodIds(recipeIds);
            requestModel.setIngredientIds(ingredientIds);
            ResponseEntity<GetPricesResponseModel> response = priceController.getPrices(requestModel);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
            assertThat(response.getHeaders().containsKey(HttpHeaders.WARNING));

        } catch (IngredientNotFoundException e) {
            fail();
        } catch (RecipeNotFoundException e) {
            fail();
        }

    }
}