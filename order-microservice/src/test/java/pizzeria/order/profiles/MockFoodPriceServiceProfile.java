package pizzeria.order.profiles;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import pizzeria.order.domain.food.FoodPriceService;

@Profile("mockPriceService")
@Configuration
public class MockFoodPriceServiceProfile {

    /**
     * Mocks the FoodPriceService.
     *
     * @return A mocked FoodPriceService.
     */
    @Bean
    @Primary  // marks this bean as the first bean to use when trying to inject an AuthenticationManager
    public FoodPriceService getMockPriceService() {
        return Mockito.mock(FoodPriceService.class);
    }
}
