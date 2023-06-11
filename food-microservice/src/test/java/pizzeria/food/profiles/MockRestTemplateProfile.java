package pizzeria.food.profiles;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.web.client.RestTemplate;

@Profile("mockRestTemplate")
@Configuration
public class MockRestTemplateProfile {
    /**
     * Mocks the TokenVerifier.
     *
     * @return A mocked TokenVerifier.
     */
    @Bean
    @Primary  // marks this bean as the first bean to use when trying to inject an AuthenticationManager
    public RestTemplate getMockRestTemplate() {
        return Mockito.mock(RestTemplate.class);
    }
}
