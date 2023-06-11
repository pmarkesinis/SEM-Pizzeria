package pizzeria.order.profiles;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import pizzeria.order.domain.order.ClockWrapper;

@Profile("clockWrapper")
@Configuration
public class ClockWrapperProfile {
    /**
     * Mocks the clockWrapper.
     *
     * @return A mocked clockWrapper
     */
    @Bean
    @Primary  // marks this bean as the first bean to use when trying to inject an AuthenticationManager
    public ClockWrapper getMockClockWrapperProfile() {
        return Mockito.mock(ClockWrapper.class);
    }
}
