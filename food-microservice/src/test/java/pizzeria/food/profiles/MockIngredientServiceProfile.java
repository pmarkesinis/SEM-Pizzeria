package pizzeria.food.profiles;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import pizzeria.food.domain.ingredient.IngredientService;

@Profile("mockIngredientService")
@Configuration
public class MockIngredientServiceProfile {

    /**
     * Mocks the IngredientService.
     *
     * @return A mocked IngredientService.
     */
    @Bean
    @Primary  // marks this bean as the first bean to use when trying to inject an AuthenticationManager
    public IngredientService getMockIngredientService() {
        return Mockito.mock(IngredientService.class);
    }
}
