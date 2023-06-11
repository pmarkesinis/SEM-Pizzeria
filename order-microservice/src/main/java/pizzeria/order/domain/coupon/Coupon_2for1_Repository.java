package pizzeria.order.domain.coupon;

import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface Coupon_2for1_Repository extends CouponRepository<TwoForOneCoupon> {
    // auto gen methods (hopefully inherited from CouponRepository interface)
}
