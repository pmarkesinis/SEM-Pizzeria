package pizzeria.order.domain.coupon;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pizzeria.order.models.CouponModel;

/**
 * The type Coupon service.
 */
@Service
public class CouponService {
    private transient final Coupon_percentage_Repository coupon_percentage_Repository;
    private transient final Coupon_2for1_Repository coupon_2for1_Repository;

    /**
     * Instantiates a new Coupon service with the respective repositories
     *
     * @param coupon_percentage_Repository the percentage coupon repository (JpaRepo)
     * @param coupon_2for1_Repository the 2for1 coupon repository (JpaRepo)
     */
    @Autowired
    public CouponService(Coupon_percentage_Repository coupon_percentage_Repository, Coupon_2for1_Repository coupon_2for1_Repository) {
        this.coupon_percentage_Repository = coupon_percentage_Repository;
        this.coupon_2for1_Repository = coupon_2for1_Repository;
    }

    /**
     * Creates a coupon in the database
     *
     * @param coupon the coupon to be created
     * @return the boolean
     */
    public boolean createCoupon(CouponModel coupon) {
        try {

            //we save the coupon in the database (if the same id is provided, the existing coupon get updated)
            switch (coupon.getType()) {
                case "PERCENTAGE":
                    PercentageCoupon percentageCoupon = new PercentageCoupon(coupon.getId(), coupon.getPercentage());
                    coupon_percentage_Repository.save(percentageCoupon);
                    break;
                case "TWO_FOR_ONE":
                    TwoForOneCoupon twoForOneCoupon = new TwoForOneCoupon(coupon.getId());
                    coupon_2for1_Repository.save(twoForOneCoupon);
                    break;
                default:
                    return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
