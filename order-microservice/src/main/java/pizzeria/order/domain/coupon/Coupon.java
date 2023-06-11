package pizzeria.order.domain.coupon;

import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import pizzeria.order.domain.order.Order;
import pizzeria.order.models.GetPricesResponseModel;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Objects;

/**
 * The type Coupon.
 */
@MappedSuperclass
public abstract class Coupon {

    /**
     * The id of the coupon
     * Serves as activation code for the customer
     */
    @Id
    @Column(name = "couponId")
    @Getter
    @Setter
    @NotNull
    protected String id;

    /**
     * Empty constructor for database purposes
     */
    public Coupon() {
    }

    /**
     * Calculate the price of an order using this coupon
     * needs to be implemented by each subclass
     *
     * @param order     the order
     * @param prices    the prices of ingredients and recipes
     * @param basePrice the base price of the order
     * @return the double
     */
    public abstract double calculatePrice(Order order, GetPricesResponseModel prices, double basePrice);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Coupon)) return false;
        Coupon coupon = (Coupon) o;
        return Objects.equals(id, coupon.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
