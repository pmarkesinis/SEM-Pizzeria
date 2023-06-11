package pizzeria.food.profiles;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import pizzeria.food.domain.recipe.RecipeService;

@Profile("mockRecipeService")
@Configuration
public class MockRecipeServiceProfile {

    /**
     * Mocks the RecipeService.
     *
     * @return A mocked RecipeService.
     */
    @Bean
    @Primary  // marks this bean as the first bean to use when trying to inject an AuthenticationManager
    public RecipeService getMockRecipeService() {
        return Mockito.mock(RecipeService.class);
    }
}
