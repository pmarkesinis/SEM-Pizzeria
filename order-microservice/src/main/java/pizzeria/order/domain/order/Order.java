package pizzeria.order.domain.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.istack.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import pizzeria.order.domain.coupon.Coupon;
import pizzeria.order.domain.food.Food;
import pizzeria.order.models.GetPricesResponseModel;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name="orders")
public class Order {

    @Id
    @Column(name = "orderId")
    @NotNull
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter
    protected Long orderId;

    @ManyToMany(fetch = FetchType.LAZY, cascade=CascadeType.ALL)
    @LazyCollection(LazyCollectionOption.FALSE)
    @Getter
    @Setter
    private List<Food> foods;

    @Column(name = "store_id")
    @Getter
    @Setter
    private long storeId;

    @Column(name = "user_id")
    @Getter
    @Setter
    private String userId;

    @Column(name = "pickup_time")
    @Getter
    @Setter
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime pickupTime;

    @Column(name = "price")
    @Getter
    @Setter
    protected double price;

    @ElementCollection
    @Column(name = "couponIds")
    @Getter
    @Setter
    protected List<String> couponIds;

    //default constructor
    public Order() {}

    /**
     * Order constructor for testing purposes
     *
     * @param orderId the order id
     * @param foods the list of foods within the order
     * @param storeId the store the order is assigned to
     * @param userId the id of the user that placed it
     * @param pickup the pickup time for the order
     * @param price the price for the order
     * @param coupons the list of applied coupon ids
     */
    public Order(Long orderId, List<Food> foods, long storeId, String userId, LocalDateTime pickup, double price, List<String> coupons){
        this.orderId = orderId;
        this.foods = foods;
        this.storeId = storeId;
        this.userId = userId;
        this.pickupTime = pickup;
        this.price = price;
        this.couponIds = coupons;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Order)) return false;
        Order order = (Order) o;
        return orderId.equals(order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    public double calculatePrice(GetPricesResponseModel prices, List<Coupon> coupons) {
        double sum = 0.0;
        for (Food f: getFoods()) {
            sum += prices.getFoodPrices().get(f.getRecipeId()).getPrice();
            for (long l: f.getExtraIngredients()) {
                sum += prices.getIngredientPrices().get(l).getPrice();
            }
        }
        return calculatePriceWithCoupons(prices, coupons, sum);
    }

    @SuppressWarnings("PMD")
    private double calculatePriceWithCoupons(GetPricesResponseModel prices, List<Coupon> coupons, double sum) {
        if (coupons.isEmpty()) {
            return sum;
        }
        final double priceWithoutCoupons = sum;
        couponIds.add("0");

        for (Coupon c: coupons) {
            //iterate over the list of valid coupons
            double price = c.calculatePrice(this, prices, priceWithoutCoupons);

            if (Double.compare(price, sum) < 0) {
                sum = price;
                //set the first element in the coupon ids to the coupon used
                //order.couponIds.clear();
                couponIds.set(0, c.getId());
            }
        }
        return sum;
    }
}
