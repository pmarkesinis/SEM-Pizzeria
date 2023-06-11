package pizzeria.food.integration;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import pizzeria.food.authentication.AuthManager;
import pizzeria.food.authentication.JwtTokenVerifier;
import pizzeria.food.domain.ingredient.Ingredient;
import pizzeria.food.domain.ingredient.IngredientRepository;
import pizzeria.food.domain.recipe.Recipe;
import pizzeria.food.domain.recipe.RecipeRepository;
import pizzeria.food.integration.utils.JsonUtil;
import pizzeria.food.models.recipe.*;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class IntegrationTestRecipeController {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient AuthManager mockAuthManager;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient RecipeRepository recipeRepository;

    @Autowired
    private transient IngredientRepository ingredientRepository;


    @BeforeEach
    public void init() {
        when(mockAuthManager.getNetId()).thenReturn("ExampleUser");
        when(mockAuthManager.getRole()).thenReturn("[ROLE_MANAGER]");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn(List.of(new SimpleGrantedAuthority("ROLE_MANAGER")));

        Ingredient ingredient1 = new Ingredient("ingredient1", 1.0);
        Ingredient ingredient2 = new Ingredient("ingredient2", 2.0);
        Ingredient ingredient3 = new Ingredient("ingredient3", 3.0);
        Ingredient ingredient4 = new Ingredient("ingredient4", 4.0);
        Ingredient ingredient5 = new Ingredient("ingredient5", 5.0);
        ingredientRepository.save(ingredient1);
        ingredientRepository.save(ingredient2);
        ingredientRepository.save(ingredient3);
        ingredientRepository.save(ingredient4);
        ingredientRepository.save(ingredient5);
    }

    @Test
    void saveFood_worksCorrectly() throws Exception {
        SaveFoodRequestModel request = new SaveFoodRequestModel();
        Recipe recipe = new Recipe("name", List.of(1L, 5L, 3L), 10.0);
        request.setRecipe(recipe);

        ResultActions resultActions = mockMvc.perform(post("/recipe/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request))
                .header("Authorization", "Bearer MockedToken"));

        MvcResult response = resultActions.andExpect(status().isCreated()).andReturn();

        SaveFoodResponseModel responseModel = JsonUtil.deserialize(response.getResponse().getContentAsString(), SaveFoodResponseModel.class);
        assertThat(responseModel.getRecipe().getName()).isEqualTo("name");
        assertThat(responseModel.getRecipe().getBaseToppings()).isEqualTo(List.of(1L, 5L, 3L));
        assertThat(responseModel.getRecipe().getBasePrice()).isEqualTo(10.0);

        Recipe recipe1 = recipeRepository.findById(6L).orElse(null);
        assertNotNull(recipe1);
        assertThat(recipe1.getName()).isEqualTo("name");
        assertThat(recipe1.getBaseToppings()).containsExactlyInAnyOrderElementsOf(List.of(1L, 5L, 3L));
        assertThat(recipe1.getBasePrice()).isEqualTo(10.0);
    }

    @Test
    void saveFoodNoIngredientId() throws Exception {
        SaveFoodRequestModel requestModel = new SaveFoodRequestModel();
        Recipe recipe = new Recipe("name", List.of(1L, 55L, 3L), 10.0);
        requestModel.setRecipe(recipe);

        ResultActions resultActions = mockMvc.perform(post("/recipe/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(requestModel))
                .header("Authorization", "Bearer MockedToken"));
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void saveRecipeNoAuthorization() throws Exception {
        when(mockAuthManager.getRole()).thenReturn("[ROLE_CUSTOMER]");
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn(List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));

        SaveFoodRequestModel request = new SaveFoodRequestModel();
        Recipe recipe = new Recipe("name", List.of(1L, 5L, 3L), 10.0);
        request.setRecipe(recipe);

        ResultActions resultActions = mockMvc.perform(post("/recipe/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request))
                .header("Authorization", "Bearer MockedToken"));
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    void saveRecipeInvalidRecipe() throws Exception {
        SaveFoodRequestModel request = new SaveFoodRequestModel();
        request.setRecipe(null);

        ResultActions resultActions = mockMvc.perform(post("/recipe/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request))
                .header("Authorization", "Bearer MockedToken"));
        resultActions.andExpect(status().isBadRequest());
    }
    @Test
    void updateFood_worksCorrectly() throws Exception {
        recipeRepository.save(new Recipe("name", List.of(1L, 5L, 3L), 10.0));
        UpdateFoodRequestModel request = new UpdateFoodRequestModel();
        Recipe recipe = new Recipe("test", List.of(3L, 5L, 4L, 2L), 12.0);
        request.setRecipe(recipe);
        request.setId(6L);

        ResultActions resultActions = mockMvc.perform(post("/recipe/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request))
                .header("Authorization", "Bearer MockedToken"));

        MvcResult response = resultActions.andExpect(status().isOk()).andReturn();
        UpdateFoodResponseModel responseModel = JsonUtil.deserialize(response.getResponse().getContentAsString(), UpdateFoodResponseModel.class);
        assertThat(responseModel.getRecipe().getName()).isEqualTo("test");
        assertThat(responseModel.getRecipe().getBaseToppings()).containsExactlyInAnyOrderElementsOf(List.of(3L, 5L, 4L, 2L));
        assertThat(responseModel.getRecipe().getBasePrice()).isEqualTo(12.0);

        Recipe recipe1 = recipeRepository.findById(6L).orElse(null);
        assertNotNull(recipe1);
        assertThat(recipe1.getName()).isEqualTo("test");
        assertThat(recipe1.getBaseToppings()).containsExactlyInAnyOrderElementsOf(List.of(3L, 5L, 4L, 2L));
        assertThat(recipe1.getBasePrice()).isEqualTo(12.0);
    }

    @Test
    void updateRecipeInvalidRecipeException() throws Exception {
        recipeRepository.save(new Recipe("name", List.of(1L, 5L, 3L), 10.0));
        UpdateFoodRequestModel request = new UpdateFoodRequestModel();
        Recipe recipe = new Recipe("", List.of(3L, 5L, 4L, 2L), 12.0);
        request.setRecipe(recipe);
        request.setId(6L);

        ResultActions resultActions = mockMvc.perform(post("/recipe/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request))
                .header("Authorization", "Bearer MockedToken"));
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void updateRecipeNotContained() throws Exception {
        UpdateFoodRequestModel request = new UpdateFoodRequestModel();
        Recipe recipe = new Recipe("", List.of(3L, 5L, 4L, 2L), 12.0);
        request.setRecipe(recipe);
        request.setId(6L);

        ResultActions resultActions = mockMvc.perform(post("/recipe/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request))
                .header("Authorization", "Bearer MockedToken"));
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void updateRecipeUnAuthorized() throws Exception {
        when(mockAuthManager.getRole()).thenReturn("[ROLE_CUSTOMER]");
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn(List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));

        UpdateFoodRequestModel request = new UpdateFoodRequestModel();
        Recipe recipe = new Recipe("name", List.of(1L, 5L, 3L), 10.0);
        request.setRecipe(recipe);
        request.setId(99L);

        ResultActions resultActions = mockMvc.perform(post("/recipe/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request))
                .header("Authorization", "Bearer MockedToken"));
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    void updateRecipeDoesNotContainIngredientId() throws Exception{
        recipeRepository.save(new Recipe("name", List.of(1L, 5L, 3L), 10.0));
        UpdateFoodRequestModel request = new UpdateFoodRequestModel();
        Recipe recipe = new Recipe("test", List.of(3L, 5L, 4L, 12L), 12.0);
        request.setRecipe(recipe);
        request.setId(6L);

        ResultActions resultActions = mockMvc.perform(post("/recipe/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request))
                .header("Authorization", "Bearer MockedToken"));
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void deleteFood_worksCorrectly() throws Exception {
        Recipe recipe = recipeRepository.save(new Recipe("name", List.of(1L, 5L, 3L), 10.0));
        DeleteFoodRequestModel request = new DeleteFoodRequestModel();
        request.setId(6L);

        ResultActions resultActions = mockMvc.perform(delete("/recipe/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request))
                .header("Authorization", "Bearer MockedToken"));
        resultActions.andExpect(status().isOk());
    }

    @Test
    void deleteFoodDoesNotContainId() throws Exception {
        DeleteFoodRequestModel request = new DeleteFoodRequestModel();
        request.setId(6L);

        ResultActions resultActions = mockMvc.perform(delete("/recipe/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request))
                .header("Authorization", "Bearer MockedToken"));
        resultActions.andExpect(status().isBadRequest());

    }

    @Test
    void deleteFoodUnauthorized() throws Exception{
        when(mockAuthManager.getRole()).thenReturn("[ROLE_CUSTOMER]");
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn(List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));

        SaveFoodRequestModel request = new SaveFoodRequestModel();
        Recipe recipe = new Recipe("name", List.of(1L, 5L, 3L), 10.0);
        request.setRecipe(recipe);

        ResultActions resultActions = mockMvc.perform(post("/recipe/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request))
                .header("Authorization", "Bearer MockedToken"));
        resultActions.andExpect(status().isForbidden());
    }


    @Test
    void getMenu_worksCorrectly() throws Exception {
        Recipe recipe1 = new Recipe("name1", List.of(1L, 5L, 3L), 10.0);
        Recipe recipe2 = new Recipe("name2", List.of(1L, 5L, 3L, 4L), 10.0);
        Recipe recipe3 = new Recipe("name3", List.of(1L, 5L, 3L, 4L, 2L), 10.0);
        recipeRepository.save(recipe1);
        recipeRepository.save(recipe2);
        recipeRepository.save(recipe3);

        ResultActions resultActions = mockMvc.perform(get("/recipe/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));
        resultActions.andExpect(status().isOk());

        MenuResponseModel responseModel = JsonUtil.deserialize(resultActions.andReturn().getResponse().getContentAsString(), MenuResponseModel.class);
        assertThat(responseModel.getMenu()).containsExactlyInAnyOrderElementsOf(List.of(recipe1, recipe2, recipe3));

    }
}
