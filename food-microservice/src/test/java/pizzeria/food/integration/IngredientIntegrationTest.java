package pizzeria.food.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import pizzeria.food.integration.utils.JsonUtil;
import pizzeria.food.models.ingredient.ExtraToppingsResponseModel;
import pizzeria.food.models.ingredient.SaveIngredientRequestModel;
import pizzeria.food.models.ingredient.SaveIngredientResponseModel;
import pizzeria.food.models.ingredient.UpdateIngredientRequestModel;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
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
public class IngredientIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient AuthManager mockAuthManager;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient IngredientRepository ingredientRepository;


    @BeforeEach
    public void init() {
        when(mockAuthManager.getNetId()).thenReturn("ExampleUser");
        when(mockAuthManager.getRole()).thenReturn("[ROLE_MANAGER]");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn(List.of(new SimpleGrantedAuthority("ROLE_MANAGER")));
    }


    @Test
    void saveIngredient_worksCorrectly() throws Exception{
        SaveIngredientRequestModel request = new SaveIngredientRequestModel();
        Ingredient testIngredient = new Ingredient("Pepperoni", 1.5, List.of("pig"));
        request.setIngredient(testIngredient);

        ResultActions resultActions = mockMvc.perform(post("/ingredient/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request))
                .header("Authorization", "Bearer MockedToken"));

        MvcResult response = resultActions.andExpect(status().isCreated()).andReturn();

        SaveIngredientResponseModel responseModel = JsonUtil.deserialize(response.getResponse().getContentAsString(), SaveIngredientResponseModel.class);

        assertThat(responseModel.getIngredient().getName()).isEqualTo("Pepperoni");
        assertThat(Double.compare(responseModel.getIngredient().getPrice(), 1.5)).isEqualTo(0);
        assertThat(responseModel.getIngredient().getAllergens().size()).isEqualTo(1);
        assertThat(responseModel.getIngredient().getAllergens().get(0)).isEqualTo("pig");

        Ingredient ingredient = ingredientRepository.findById(1L).orElse(null);

        assertThat(ingredient).isNotNull();
        assertThat(ingredient.getName()).isEqualTo("Pepperoni");
        assertThat(Double.compare(ingredient.getPrice(), 1.5)).isEqualTo(0);
        assertThat(ingredient.getAllergens().size()).isEqualTo(1);
        assertThat(ingredient.getAllergens().get(0)).isEqualTo("pig");
    }

    @Test
    void saveIngredient_noAuthorization() throws Exception{
        //set up the wrong role
        when(mockAuthManager.getRole()).thenReturn("[ROLE_CUSTOMER]");
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn(List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));

        SaveIngredientRequestModel request = new SaveIngredientRequestModel();
        Ingredient testIngredient = new Ingredient("Pepperoni", 1.5, List.of("pig"));
        request.setIngredient(testIngredient);

        ResultActions resultActions = mockMvc.perform(post("/ingredient/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request))
                .header("Authorization", "Bearer MockedToken"));

        resultActions.andExpect(status().isForbidden());
    }

    @Test
    void updateIngredient_worksCorrectly() throws Exception {
        //first we add it to the database
        SaveIngredientRequestModel request1 = new SaveIngredientRequestModel();
        Ingredient testIngredient1 = new Ingredient("Pepperoni", 1.5, List.of("pig"));
        request1.setIngredient(testIngredient1);

        ResultActions resultActions1 = mockMvc.perform(post("/ingredient/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request1))
                .header("Authorization", "Bearer MockedToken"));

        resultActions1.andExpect(status().isCreated());

        UpdateIngredientRequestModel request2 = new UpdateIngredientRequestModel();
        Ingredient testIngredient2 = new Ingredient("Olive", 1.0);
        request2.setIngredient(testIngredient2);
        request2.setId(1L);

        ResultActions resultActions2 = mockMvc.perform(post("/ingredient/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request2))
                .header("Authorization", "Bearer MockedToken"));

        MvcResult response = resultActions2.andExpect(status().isCreated()).andReturn();

        SaveIngredientResponseModel responseModel = JsonUtil.deserialize(response.getResponse().getContentAsString(), SaveIngredientResponseModel.class);

        assertThat(responseModel.getIngredient().getName()).isEqualTo("Olive");
        assertThat(Double.compare(responseModel.getIngredient().getPrice(), 1.0)).isEqualTo(0);
        assertThat(responseModel.getIngredient().getAllergens().size()).isEqualTo(0);

        Ingredient ingredient = ingredientRepository.findById(1L).orElse(null);

        assertThat(ingredient).isNotNull();
        assertThat(ingredient.getName()).isEqualTo("Olive");
        assertThat(Double.compare(ingredient.getPrice(), 1.0)).isEqualTo(0);
        assertThat(ingredient.getAllergens().size()).isEqualTo(0);
    }

    @Test
    void updateIngredient_noAuthorization() throws Exception{
        SaveIngredientRequestModel request1 = new SaveIngredientRequestModel();
        Ingredient testIngredient1 = new Ingredient("Pepperoni", 1.5, List.of("pig"));
        request1.setIngredient(testIngredient1);

        ResultActions resultActions1 = mockMvc.perform(post("/ingredient/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request1))
                .header("Authorization", "Bearer MockedToken"));

        resultActions1.andExpect(status().isCreated());

        //set up the wrong role
        when(mockAuthManager.getRole()).thenReturn("[ROLE_CUSTOMER]");
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn(List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));

        UpdateIngredientRequestModel request2 = new UpdateIngredientRequestModel();
        Ingredient testIngredient2 = new Ingredient("Olive", 1.0);
        request2.setIngredient(testIngredient2);
        request2.setId(1L);

        ResultActions resultActions2 = mockMvc.perform(post("/ingredient/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request2))
                .header("Authorization", "Bearer MockedToken"));

        resultActions2.andExpect(status().isForbidden());

        //assert that the ingredient was not updated
        Ingredient ingredient = ingredientRepository.findById(1L).orElse(null);

        assertThat(ingredient).isNotNull();
        assertThat(ingredient.getName()).isEqualTo("Pepperoni");
        assertThat(Double.compare(ingredient.getPrice(), 1.5)).isEqualTo(0);
        assertThat(ingredient.getAllergens().size()).isEqualTo(1);
        assertThat(ingredient.getAllergens().get(0)).isEqualTo("pig");
    }

    @Test
    void deleteIngredient_worksCorrectly() throws Exception{
        //first we add it to the database
        SaveIngredientRequestModel request1 = new SaveIngredientRequestModel();
        Ingredient testIngredient1 = new Ingredient("Pepperoni", 1.5, List.of("pig"));
        request1.setIngredient(testIngredient1);

        ResultActions resultActions1 = mockMvc.perform(post("/ingredient/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request1))
                .header("Authorization", "Bearer MockedToken"));

        resultActions1.andExpect(status().isCreated());

        UpdateIngredientRequestModel request2 = new UpdateIngredientRequestModel();
        request2.setId(1L);

        ResultActions resultActions2 = mockMvc.perform(delete("/ingredient/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request2))
                .header("Authorization", "Bearer MockedToken"));

        resultActions2.andExpect(status().isOk());

        Ingredient ingredient = ingredientRepository.findById(1L).orElse(null);
        //assert it was deleted
        assertThat(ingredient).isNull();
    }

    @Test
    void deleteIngredient_noAuthorization() throws Exception{
        //first we add it to the database
        SaveIngredientRequestModel request1 = new SaveIngredientRequestModel();
        Ingredient testIngredient1 = new Ingredient("Pepperoni", 1.5, List.of("pig"));
        request1.setIngredient(testIngredient1);

        ResultActions resultActions1 = mockMvc.perform(post("/ingredient/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request1))
                .header("Authorization", "Bearer MockedToken"));

        resultActions1.andExpect(status().isCreated());

        //set up the wrong role
        when(mockAuthManager.getRole()).thenReturn("[ROLE_CUSTOMER]");
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn(List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));

        UpdateIngredientRequestModel request2 = new UpdateIngredientRequestModel();
        request2.setId(1L);

        ResultActions resultActions2 = mockMvc.perform(delete("/ingredient/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request2))
                .header("Authorization", "Bearer MockedToken"));

        resultActions2.andExpect(status().isForbidden());

        Ingredient ingredient = ingredientRepository.findById(1L).orElse(null);
        //assert it is still there
        assertThat(ingredient).isNotNull();
        assertThat(ingredient.getName()).isEqualTo("Pepperoni");
        assertThat(Double.compare(ingredient.getPrice(), 1.5)).isEqualTo(0);
        assertThat(ingredient.getAllergens().size()).isEqualTo(1);
        assertThat(ingredient.getAllergens().get(0)).isEqualTo("pig");
    }

    @Test
    void getExtraToppingsSet_worksCorrectly() throws Exception{
        //add some ingredients
        Ingredient testIngredient1 = new Ingredient("Pepperoni", 1.5, List.of("pig"));
        Ingredient testIngredient2 = new Ingredient("Olive", 1.0);
        Ingredient testIngredient3 = new Ingredient("Mozzarella", 2.0, List.of("dairy"));
        Ingredient testIngredient4 = new Ingredient("Fries", 4.0, List.of("gluten", "potato"));
        Ingredient testIngredient5 = new Ingredient("Basil", 0.5);

        List<Ingredient> testIngredients = List.of(testIngredient1, testIngredient2, testIngredient3, testIngredient4, testIngredient5);
        for (Ingredient i : testIngredients) {
            SaveIngredientRequestModel request = new SaveIngredientRequestModel();
            request.setIngredient(i);
            ResultActions resultActions = mockMvc.perform(post("/ingredient/save")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.serialize(request))
                    .header("Authorization", "Bearer MockedToken"));

            resultActions.andExpect(status().isCreated());
        }

        //test that everything is given back
        ResultActions resultActions = mockMvc.perform(get("/ingredient/extraToppings")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        MvcResult response = resultActions.andExpect(status().isOk()).andReturn();

        ExtraToppingsResponseModel responseModel = JsonUtil.deserialize(response.getResponse().getContentAsString(), ExtraToppingsResponseModel.class);

        assertThat(responseModel.getIngredients().size()).isEqualTo(5);

        for (int i = 0; i < 5; i++){
            Ingredient current = responseModel.getIngredients().get(i);
            assertThat(current).isNotNull();
            assertThat(current.getName()).isEqualTo(testIngredients.get(i).getName());
            assertThat(current.getPrice()).isEqualTo(testIngredients.get(i).getPrice());
            assertThat(current.getId()).isEqualTo(i + 1L);
            assertThat(current.getAllergens()).isEqualTo(testIngredients.get(i).getAllergens());
        }
    }

    @Test
    void getExtraToppingsSet_accessibleToEveryone() throws Exception{
        //add some ingredients
        Ingredient testIngredient1 = new Ingredient("Pepperoni", 1.5, List.of("pig"));
        Ingredient testIngredient2 = new Ingredient("Olive", 1.0);
        Ingredient testIngredient3 = new Ingredient("Mozzarella", 2.0, List.of("dairy"));
        Ingredient testIngredient4 = new Ingredient("Fries", 4.0, List.of("gluten", "potato"));
        Ingredient testIngredient5 = new Ingredient("Basil", 0.5);

        List<Ingredient> testIngredients = List.of(testIngredient1, testIngredient2, testIngredient3, testIngredient4, testIngredient5);
        for (Ingredient i : testIngredients) {
            SaveIngredientRequestModel request = new SaveIngredientRequestModel();
            request.setIngredient(i);
            ResultActions resultActions = mockMvc.perform(post("/ingredient/save")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(JsonUtil.serialize(request))
                    .header("Authorization", "Bearer MockedToken"));

            resultActions.andExpect(status().isCreated());
        }

        //set up the customer role
        when(mockAuthManager.getRole()).thenReturn("[ROLE_CUSTOMER]");
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn(List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));

        //test that everything is given back
        ResultActions resultActions = mockMvc.perform(get("/ingredient/extraToppings")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        MvcResult response = resultActions.andExpect(status().isOk()).andReturn();

        ExtraToppingsResponseModel responseModel = JsonUtil.deserialize(response.getResponse().getContentAsString(), ExtraToppingsResponseModel.class);

        assertThat(responseModel.getIngredients().size()).isEqualTo(5);

        for (int i = 0; i < 5; i++){
            Ingredient current = responseModel.getIngredients().get(i);
            assertThat(current).isNotNull();
            assertThat(current.getName()).isEqualTo(testIngredients.get(i).getName());
            assertThat(current.getPrice()).isEqualTo(testIngredients.get(i).getPrice());
            assertThat(current.getId()).isEqualTo(i + 1L);
            assertThat(current.getAllergens()).isEqualTo(testIngredients.get(i).getAllergens());
        }
    }

    @ParameterizedTest
    @MethodSource("incorrectIngredientsSuite")
    void testIncorrectIngredients(Ingredient testIngredient) throws Exception{
        //insert the ingredient in the DB
        SaveIngredientRequestModel request = new SaveIngredientRequestModel();
        //make an ingredient with a matching name of the second
        Ingredient ing = new Ingredient("Olive", 1.0);
        request.setIngredient(ing);

        ResultActions resultActions = mockMvc.perform(post("/ingredient/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request))
                .header("Authorization", "Bearer MockedToken"));

        resultActions.andExpect(status().isCreated());

        //insert the test ingredient, should not work
        SaveIngredientRequestModel testRequest = new SaveIngredientRequestModel();
        testRequest.setIngredient(testIngredient);

        ResultActions resultActions2 = mockMvc.perform(post("/ingredient/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(testRequest))
                .header("Authorization", "Bearer MockedToken"));

        resultActions2.andExpect(status().isBadRequest());
    }

    static Stream<Arguments> incorrectIngredientsSuite() {
        Ingredient testIngredient1 = new Ingredient("Pepperoni", 1.5, List.of("pig"));
        testIngredient1.setId(1L);
        Ingredient testIngredient2 = new Ingredient("Olive", 1.0);

        return Stream.of(
                Arguments.of(testIngredient1),
                Arguments.of(testIngredient2)
        );
    }

    @Test
    void testIncorrectIdUpdate() throws Exception{
        //now try to update an ingredient that does not exist
        UpdateIngredientRequestModel request2 = new UpdateIngredientRequestModel();
        Ingredient testIngredient2 = new Ingredient("Olive", 1.0);
        request2.setIngredient(testIngredient2);
        request2.setId(3L);

        ResultActions resultActions2 = mockMvc.perform(post("/ingredient/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request2))
                .header("Authorization", "Bearer MockedToken"));

        resultActions2.andExpect(status().isBadRequest());
    }

    @Test
    void testIncorrectDelete() throws Exception{
        //now try to delete an ingredient that does not exist
        UpdateIngredientRequestModel request2 = new UpdateIngredientRequestModel();
        request2.setId(3L);

        ResultActions resultActions2 = mockMvc.perform(delete("/ingredient/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(request2))
                .header("Authorization", "Bearer MockedToken"));

        resultActions2.andExpect(status().isBadRequest());
    }
}
