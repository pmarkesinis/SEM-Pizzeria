package pizzeria.order.domain.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pizzeria.order.domain.coupon.Coupon_2for1_Repository;
import pizzeria.order.domain.coupon.Coupon_percentage_Repository;
import pizzeria.order.domain.coupon.Coupon;
import pizzeria.order.domain.food.FoodPriceService;
import pizzeria.order.domain.mailing.MailingService;
import pizzeria.order.domain.store.StoreService;
import pizzeria.order.models.GetPricesResponseModel;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The type Order service
 * Handles interaction with the endpoints and repositories, contains most verification logic
 */
@Service
public class OrderService {
    private final transient OrderRepository orderRepo;
    private final transient FoodPriceService foodPriceService;
    private transient final Coupon_percentage_Repository coupon_percentage_repository;
    private transient final Coupon_2for1_Repository coupon_2for1_repository;
    private transient final ClockWrapper clockWrapper;

    private final transient StoreService storeService;

    private final transient MailingService mailingService;

    /**
     * Instantiates a new Order service with the respective repositories and services
     *
     * @param orderRepo        the order repository
     * @param foodPriceService the food price service
     * @param clockWrapper     clock wrapper for time
     * @param storeService     the store service
     * @param coupon_percentage_repository the percentage coupon repository
     * @param coupon_2for1_repository the 2for1 coupon repository
     */
    @Autowired
    public OrderService(OrderRepository orderRepo, FoodPriceService foodPriceService,
                        ClockWrapper clockWrapper, StoreService storeService,
                        Coupon_2for1_Repository coupon_2for1_repository,
                        Coupon_percentage_Repository coupon_percentage_repository,
                        MailingService mailingService){
        this.orderRepo = orderRepo;
        this.foodPriceService = foodPriceService;
        this.clockWrapper = clockWrapper;
        this.storeService = storeService;
        this.coupon_percentage_repository = coupon_percentage_repository;
        this.coupon_2for1_repository = coupon_2for1_repository;
        this.mailingService = mailingService;
    }

    /**
     * Process an order 
     * Includes validation of user, of time, of price, of foods and of coupons
     *
     * @param order the order to be processed
     * @return the order, after processing
     * @throws OrderServiceExceptions.PriceNotRightException something went wrong with the price calculation on the user end
     * @throws OrderServiceExceptions.TimeInvalidException the selected pickup time does not pass the criteria
     * @throws OrderServiceExceptions.CouldNotStoreException there was an error with the order passed so it cannot be stored in the database
     * @throws OrderServiceExceptions.FoodInvalidException there are invalid or non-existing foods in the order placed
     * @throws OrderServiceExceptions.InvalidEditException exclusively when editing an order, the order does not belong to the user
     *
     */
    @SuppressWarnings("PMD")
    public Order processOrder(Order order) throws Exception {
        validateInput(order);

        validateOrderTime(order);

        GetPricesResponseModel prices = foodPriceService.getFoodPrices(order); // get prices
        if (prices == null)
            //some food does not exist or something else went wrong in the food ms communication
            throw new OrderServiceExceptions.FoodInvalidException();

        return calculatePrice(order, prices);
    }

    @SuppressWarnings("PMD")
    private Order calculatePrice(Order order, GetPricesResponseModel prices) throws OrderServiceExceptions.PriceNotRightException {
        ArrayList<Coupon> coupons = new ArrayList<>(coupon_percentage_repository.findAllById(order.couponIds));
        coupons.addAll(coupon_2for1_repository.findAllById(order.couponIds));
        // this list only contains validated coupons, no need for additional checks
        order.couponIds.clear(); // clear the list, so we can send only the used one back
        //get the base price of the order
        double sum = order.calculatePrice(prices, coupons);

        //sum = calculatePriceWithCoupons(order, prices, coupons);
        final double EPS = 1e-6;
        if (Math.abs(order.price - sum) > EPS) {
            throw new OrderServiceExceptions.PriceNotRightException("Price is not right");
        }
        return orderRepo.save(order);
    }

    private void validateOrderTime(Order order) throws OrderServiceExceptions.TimeInvalidException {
        //check if the selected pickup time is 30 minutes or more in the future
        LocalDateTime current = clockWrapper.getNow();

        if (order.getPickupTime().isBefore(current.plusMinutes(30)))
            throw new OrderServiceExceptions.TimeInvalidException();
    }

    private void validateInput(Order order) throws Exception {
        // null-checks for all members
        if (order == null || order.getFoods() == null || order.getUserId() == null
                || order.getPickupTime() == null || order.getCouponIds() == null)
            throw new OrderServiceExceptions.CouldNotStoreException();
        // check if we are in 'edit mode' (the orderId is specified in the Order object)
        // then check if the order belongs to the user
        //when we find by id we return an optional, if for some reason this optional does not exist return new order, which has null fields for non-primitives
        //essentially check if the order is in the repo and belongs to the person trying to edit
        if (order.orderId != null && !order.getUserId().equals(orderRepo.findById(order.orderId).orElse(new Order()).getUserId())) {
            //System.out.println(order.getUserId() + " " + orderRepo.findByOrderId(order.orderId));
            throw new OrderServiceExceptions.InvalidEditException();
        }

        if (!storeService.getStoreRepo().existsById(order.getStoreId())) {
            throw new OrderServiceExceptions.InvalidStoreIdException();
        }
    }

    /**
     * Remove an order
     * Includes user validation or check if we have a manager requesting this, also check time constraint
     *
     * @param orderId   the order id
     * @param userId    the user id
     * @param isManager the is manager
     * @return the boolean
     */
    public boolean removeOrder(Long orderId, String userId, boolean isManager) {
        if (orderId == null || userId == null)
            return false;
        //check if the order exists
        Order toDelete = orderRepo.findById(orderId).orElse(null);
        if (toDelete == null){
            return false;
        }

        try {
            validateOrderTime(toDelete);
        } catch (OrderServiceExceptions.TimeInvalidException e) {
            return false;
        }

        //check if we have a manager or the order belongs to the user
        //we already check above that the order exists, so now we only check if the user ids match
        if (isManager || userId.equals(toDelete.getUserId())) {
            orderRepo.deleteById(orderId);
            return true;
        }

        return false;
    }

    /**
     * Returns the order given its id
     * @param id id of the order
     * @return Optional of type order
     */
    public Optional<Order> findOrder(Long id) {
        return orderRepo.findByOrderId(id);
    }

    /**
     * List orders, finds all the orders matching the user id in the repo
     *
     * @param userId the user id to find orders for
     * @return the list of orders
     */
    public List<Order> listOrders(String userId) {
        return orderRepo.findByUserId(userId);
    }

    /**
     * List all orders, gets all the orders from the database
     *
     * @return the list of orders
     */
    public List<Order> listAllOrders() {
        return orderRepo.findAll();
    }
}
