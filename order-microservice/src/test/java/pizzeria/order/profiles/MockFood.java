package pizzeria.order.profiles;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import pizzeria.order.domain.food.Food;

@Profile("mockFood")
@Configuration
public class MockFood {

    @Bean
    @Primary
    public Food getMockFood() {
        return Mockito.mock(Food.class);
    }
}
