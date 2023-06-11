package pizzeria.order.domain.coupon;

import pizzeria.order.domain.food.Food;
import pizzeria.order.domain.order.Order;
import pizzeria.order.models.GetPricesResponseModel;

import javax.persistence.Entity;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Two for one coupon.
 */
@Entity
public class TwoForOneCoupon extends Coupon {

    /**
     * Empty constructor for database purposes
     */
    public TwoForOneCoupon() {
        super();
    }

    public TwoForOneCoupon(String id){
        this.id = id;
    }


    /**
     * Calculates the price of an order using the 2for1 coupon
     *
     * @param order     the order to evaluate price on
     * @param prices    the prices of ingredients and recipes
     * @param basePrice the base price of the order
     * @return the final price of the order after applying the coupon
     */
    @Override
    public double calculatePrice(Order order, GetPricesResponseModel prices, double basePrice) {
        //make a hashmap that keeps track of every food in the list with its occurrences

        Map<Long, Integer> foodMap = new HashMap<>();
        for (Food f : order.getFoods()){
            //if this recipe is already in the choices, add an occurrence else put it with 1 occurrence
            if (!foodMap.containsKey(f.getRecipeId())){
                foodMap.put(f.getRecipeId(), 1);
            }else {
                foodMap.put(f.getRecipeId(), foodMap.get(f.getRecipeId()) + 1);
            }
        }

        //now every time we have 2 times an item we only charge it once
        //so essentially we subtract from the price (occurrences)/2 per recipe
        //this coupon could be made a little more interesting if we add a recipe id (like margherita pizza)
        double reduction = 0.0;
        for (Long key : foodMap.keySet()){
            int reduced_times = foodMap.get(key) / 2;
            reduction += (double) reduced_times * prices.getFoodPrices().get(key).getPrice();
        }

        //return the base price - the reduction
        return basePrice - reduction;
    }
}
