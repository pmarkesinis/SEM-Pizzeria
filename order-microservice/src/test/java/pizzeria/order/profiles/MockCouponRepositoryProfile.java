package pizzeria.order.profiles;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import pizzeria.order.domain.coupon.CouponRepository;

@Profile("mockCouponRepository")
@Configuration
public class MockCouponRepositoryProfile {
    @Bean
    @Primary
    public CouponRepository getMockCouponRepository() {
        return Mockito.mock(CouponRepository.class);
    }
}
