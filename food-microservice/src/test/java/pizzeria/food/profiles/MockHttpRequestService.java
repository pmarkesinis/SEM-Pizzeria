package pizzeria.food.profiles;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import pizzeria.food.communication.HttpRequestService;

@Profile("mockHttpRequestService")
@Configuration
public class MockHttpRequestService {
    @Bean
    @Primary  // marks this bean as the first bean to use when trying to inject an HttpRequestService
    public HttpRequestService getMockHttpRequestService() {
        return Mockito.mock(HttpRequestService.class);
    }
}
