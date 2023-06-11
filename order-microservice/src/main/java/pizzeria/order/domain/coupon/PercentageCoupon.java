package pizzeria.order.domain.coupon;

import lombok.Getter;
import lombok.Setter;
import pizzeria.order.domain.order.Order;
import pizzeria.order.models.GetPricesResponseModel;

import javax.persistence.Entity;

/**
 * The type Percentage coupon.
 */
@Entity
public class PercentageCoupon extends Coupon {

    @Getter
    @Setter
    private double percentage;

    /**
     * Empty constructor, database purposes
     */
    public PercentageCoupon() {
        super();
    }

    /**
     * PercentageCoupon constructor for testing purposes
     *
     * @param id the id of the coupon (activation code)
     * @param percentage the percentage that is to be discounted, e.g. 0.15 -> 15%, 0 <= p <= 1
     */
    public PercentageCoupon(String id, double percentage){
        if (percentage > 1 || percentage < 0) throw new IllegalArgumentException();
        this.id = id;
        this.percentage = percentage;
    }

    /**
     * Calculates the price of an order using the percentage coupon
     * Validates that the percentage is within 0 and 1
     *
     * @param order     the order to evaluate price on
     * @param prices    the prices of ingredients and recipes
     * @param basePrice the base price of the order
     * @return the final price of the order after applying the coupon
     */
    @Override
    public double calculatePrice(Order order, GetPricesResponseModel prices, double basePrice) {
        //makes the percentage reduction on the base price
        return basePrice - basePrice * percentage;
    }
}
