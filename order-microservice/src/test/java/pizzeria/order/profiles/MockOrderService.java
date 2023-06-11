package pizzeria.order.profiles;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import pizzeria.order.domain.order.OrderService;

@Profile("mockOrderService")
@Configuration
public class MockOrderService {

    @Bean
    @Primary
    public OrderService getMockOrderService() {
        return Mockito.mock(OrderService.class);
    }
}