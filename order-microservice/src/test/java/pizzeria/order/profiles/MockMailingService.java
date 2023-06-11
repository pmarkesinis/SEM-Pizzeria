package pizzeria.order.profiles;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import pizzeria.order.domain.mailing.MailingService;

@Profile("mockMailingService")
@Configuration
public class MockMailingService {

    @Bean
    @Primary
    public MailingService getMockMailingService() {
        return Mockito.mock(MailingService.class);
    }
}