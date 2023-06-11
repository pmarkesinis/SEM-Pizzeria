package pizzeria.order.profiles;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import pizzeria.order.domain.store.StoreService;

@Profile("mockStoreService")
@Configuration
public class MockStoreServiceProfile {
    @Bean
    @Primary
    public StoreService getMockStoreService() {
        return Mockito.mock(StoreService.class);
    }
}
