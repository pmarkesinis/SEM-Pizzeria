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
import pizzeria.food.communication.HttpRequestService;
import pizzeria.food.domain.ingredient.Ingredient;
import pizzeria.food.domain.ingredient.IngredientRepository;
import pizzeria.food.domain.recipe.Recipe;
import pizzeria.food.domain.recipe.RecipeRepository;
import pizzeria.food.integration.utils.JsonUtil;
import pizzeria.food.models.allergens.FilterMenuRequestModel;
import pizzeria.food.models.allergens.FilterMenuResponseModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockHttpRequestService", "mockTokenVerifier", "mockAuthenticationManager"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class AllergenIntegrationTestController {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient RecipeRepository recipeRepository;

    @Autowired
    private transient IngredientRepository ingredientRepository;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient HttpRequestService httpRequestService;

    @Autowired
    private transient AuthManager mockAuthManager;

    @BeforeEach
    public void init() {
        when(mockAuthManager.getNetId()).thenReturn("ExampleUser");
        when(mockAuthManager.getRole()).thenReturn("[ROLE_MANAGER]");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn(List.of(new SimpleGrantedAuthority("ROLE_MANAGER")));

        //add ingredients to the repo
        Ingredient tomato = new Ingredient("tomato", 0.5);
        Ingredient mozzarella = new Ingredient("mozzarella", 1.0, List.of("dairy"));
        Ingredient lactose_free_mozzarella = new Ingredient("lactose_free_mozzarella", 1.5);
        Ingredient salami = new Ingredient("salami", 1.0, List.of("pig"));
        Ingredient black_olive = new Ingredient("black_olive", 0.5);
        ingredientRepository.save(tomato);
        ingredientRepository.save(mozzarella);
        ingredientRepository.save(lactose_free_mozzarella);
        ingredientRepository.save(salami);
        ingredientRepository.save(black_olive);

        //add recipes to the repo
        Recipe margherita = new Recipe("margherita", List.of(1L, 2L, 5L), 7.0);
        Recipe salamiPizza = new Recipe("salamiPizza", List.of(1L, 2L, 4L), 8.0);
        Recipe lactose_free_margherita = new Recipe("lactose_free_margherita", List.of(1L, 3L, 5L), 8.0);
        recipeRepository.save(margherita);
        recipeRepository.save(salamiPizza);
        recipeRepository.save(lactose_free_margherita);
    }

    //if you have a token (are authenticated) then you get the allergens back
    //else unauthorized

    //do not mock the repos
    //REPOS: add recipes
    //REPOS: add ingredients (with allergens)
    //if we have allergens (might be empty list even): first we go through the recipes
    //List<Recipe> findAll on recipe repo
    //then per recipe go through all ingredients (might not find ingredient in the repo, throw exception)
    //catch the exception in the controller

    //test cases: first one where everything works, no allergens
    //then everything works but we have allergens
    //then unauthorized
    //then things work but we have an illegal ingredient somewhere

    @Test
    void filterMenu_worksCorrectly_noAllergens() throws Exception{
        FilterMenuRequestModel requestModel = new FilterMenuRequestModel();
        List<String> allergens = new ArrayList<>();
        requestModel.setAllergens(allergens);

        //make a test where this returns empty optional (unauthorized)
        when(httpRequestService.getUserAllergens(any())).thenReturn(Optional.of(allergens));

        ResultActions resultActions = mockMvc.perform(get("/allergens/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(requestModel))
                .header("Authorization", "Bearer MockedToken"));

        MvcResult response = resultActions.andExpect(status().isOk()).andReturn();

        FilterMenuResponseModel responseModel = JsonUtil.deserialize(response.getResponse().getContentAsString(), FilterMenuResponseModel.class);

        //we will get the whole menu
        assertThat(responseModel.getRecipes().size()).isEqualTo(3);
        assertThat(responseModel.getRecipes().get(0).getName()).isEqualTo("margherita");
        assertThat(responseModel.getRecipes().get(1).getName()).isEqualTo("salamiPizza");
        assertThat(responseModel.getRecipes().get(2).getName()).isEqualTo("lactose_free_margherita");
    }

    @Test
    void filterMenu_worksCorrectly_withAllergens() throws Exception{
        FilterMenuRequestModel requestModel = new FilterMenuRequestModel();
        List<String> allergens = List.of("pig", "dairy");
        requestModel.setAllergens(allergens);

        //make a test where this returns empty optional (unauthorized)
        when(httpRequestService.getUserAllergens(any())).thenReturn(Optional.of(allergens));

        ResultActions resultActions = mockMvc.perform(get("/allergens/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(requestModel))
                .header("Authorization", "Bearer MockedToken"));

        MvcResult response = resultActions.andExpect(status().isOk()).andReturn();

        FilterMenuResponseModel responseModel = JsonUtil.deserialize(response.getResponse().getContentAsString(), FilterMenuResponseModel.class);

        //we will get the whole menu
        assertThat(responseModel.getRecipes().size()).isEqualTo(1);
        assertThat(responseModel.getRecipes().get(0).getName()).isEqualTo("lactose_free_margherita");
    }

    @Test
    void filterMenu_unauthorized() throws Exception{
        FilterMenuRequestModel requestModel = new FilterMenuRequestModel();
        List<String> allergens = new ArrayList<>();
        requestModel.setAllergens(allergens);

        //this is when the user is unauthorized
        when(httpRequestService.getUserAllergens(any())).thenReturn(Optional.empty());

        ResultActions resultActions = mockMvc.perform(get("/allergens/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(requestModel))
                .header("Authorization", "Bearer MockedToken"));

        resultActions.andExpect(status().isUnauthorized());
    }

    @Test
    void filterMenu_illegalIngredient() throws Exception{
        FilterMenuRequestModel requestModel = new FilterMenuRequestModel();
        List<String> allergens = new ArrayList<>();
        requestModel.setAllergens(allergens);

        //save a new recipe with the id of an ingredient that does not exist
        Recipe failer = new Recipe("failer", List.of(1L, 2L, 8L), 7.0);
        recipeRepository.save(failer);

        //make a test where this returns empty optional (unauthorized)
        when(httpRequestService.getUserAllergens(any())).thenReturn(Optional.of(allergens));

        ResultActions resultActions = mockMvc.perform(get("/allergens/menu")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(requestModel))
                .header("Authorization", "Bearer MockedToken"));

        resultActions.andExpect(status().isBadRequest());
    }
}
