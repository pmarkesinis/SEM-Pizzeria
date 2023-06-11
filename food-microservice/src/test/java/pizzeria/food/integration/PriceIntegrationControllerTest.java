package pizzeria.food.integration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import pizzeria.food.domain.ingredient.IngredientNotFoundException;
import pizzeria.food.domain.ingredient.IngredientService;
import pizzeria.food.domain.recipe.RecipeNotFoundException;
import pizzeria.food.domain.recipe.RecipeService;
import pizzeria.food.domain.recipe.RecipeServiceResponseInformation;
import pizzeria.food.integration.utils.JsonUtil;
import pizzeria.food.models.prices.GetPricesRequestModel;
import pizzeria.food.models.prices.GetPricesResponseModel;
import pizzeria.food.models.prices.Tuple;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles({"test", "mockRecipeService", "mockRecipeResponseService", "mockIngredientService"})
@AutoConfigureMockMvc
class PriceIntegrationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private transient RecipeService recipeService;
    @Autowired
    private transient IngredientService ingredientService;
    @Autowired
    private transient RecipeServiceResponseInformation recipeServiceResponseInformation;

    @Test
    void getPricesWorks() throws Exception {

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

        GetPricesRequestModel requestModel = new GetPricesRequestModel();
        requestModel.setFoodIds(recipeIds);
        requestModel.setIngredientIds(ingredientIds);

        when(recipeServiceResponseInformation.getPrices(any())).thenReturn(recipePrices);
        when(ingredientService.getDetails(any())).thenReturn(ingredientPrices);

        ResultActions resultActions = mockMvc.perform(post("/price/ids")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(requestModel))
                .header("Authorization", "Bearer MockedToken"));

        resultActions.andExpect(status().isOk());
        GetPricesResponseModel responseModel = JsonUtil.deserialize(resultActions.andReturn().getResponse().getContentAsString(), GetPricesResponseModel.class);

        assertNotNull(responseModel);
        assertThat(responseModel.getFoodPrices()).isEqualTo(recipePrices);
        assertThat(responseModel.getIngredientPrices()).isEqualTo(ingredientPrices);
    }

    //@Test
    void testGetPricesThrowsExceptionRecipeNotFound() throws Exception {
        List<Long> recipeIds = List.of(5L, 8L, 88L);
        List<Long> ingredientIds = List.of(52L, 4L, 78L, 99L);

        Map<Long, Tuple> ingredientPrices = Map.of(
                52L, new Tuple(52.0, "Test"),
                4L, new Tuple(4.0, "Test1"),
                78L, new Tuple(78.0, "Test2"),
                99L, new Tuple(99.0, "Test3")
        );


        when(recipeServiceResponseInformation.getPrices(recipeIds)).thenThrow(new RecipeNotFoundException());
        when(ingredientService.getDetails(any())).thenReturn(ingredientPrices);

        GetPricesRequestModel requestModel = new GetPricesRequestModel();
        requestModel.setFoodIds(recipeIds);
        requestModel.setIngredientIds(ingredientIds);

        ResultActions resultActions = mockMvc.perform(post("/price/ids")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(requestModel))
                .header("Authorization", "Bearer MockedToken"));

        resultActions.andExpect(status().isBadRequest());

        MockHttpServletResponse response = resultActions.andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(400); // BAD REQUEST
        assertThat(response.getHeader("Warning")).isEqualTo("The recipe could not be found in the database");
    }

    //@Test
    void testGetPricesThrowsIngredientNotFoundException() throws Exception {
        List<Long> recipeIds = List.of(5L, 8L, 88L);
        List<Long> ingredientIds = List.of(52L, 4L, 78L, 99L);

        Map<Long, Tuple> recipePrices = Map.of(
                5L, new Tuple(5.0, "Test"),
                8L, new Tuple(8.0, "Test1"),
                88L, new Tuple(88.0, "Test2")
        );

        when(recipeServiceResponseInformation.getPrices(recipeIds)).thenReturn(recipePrices);
        when(ingredientService.getDetails(ingredientIds)).thenThrow(new IngredientNotFoundException());

        GetPricesRequestModel requestModel = new GetPricesRequestModel();
        requestModel.setFoodIds(recipeIds);
        requestModel.setIngredientIds(ingredientIds);

        ResultActions resultActions = mockMvc.perform(post("/price/ids")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(requestModel))
                .header("Authorization", "Bearer MockedToken"));

        resultActions.andExpect(status().isBadRequest());

        MockHttpServletResponse response = resultActions.andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(400); // BAD REQUEST
        assertThat(response.getHeader("Warning")).isEqualTo("The ingredient was not found in the database");
    }
}