package pizzeria.order.profiles;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import pizzeria.order.domain.mailing.MessageTransport;

@Profile("mockMessageTransport")
@Configuration
public class MockMessageTransportProfile {
    @Bean
    @Primary
    public MessageTransport getMockMessageTransport() {
        return Mockito.mock(MessageTransport.class);
    }
}
