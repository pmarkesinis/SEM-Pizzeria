package pizzeria.order.domain.coupon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface CouponRepository<T extends Coupon> extends JpaRepository<T, String> {

    Optional<T> findById(String id);
}

