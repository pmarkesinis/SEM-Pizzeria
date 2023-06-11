package pizzeria.order.controllers;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pizzeria.order.authentication.AuthManager;
import pizzeria.order.domain.mailing.MailingService;
import pizzeria.order.domain.order.Order;
import pizzeria.order.domain.order.OrderOperationService;
import pizzeria.order.domain.order.OrderService;
import pizzeria.order.models.DeleteModel;
import pizzeria.order.models.OrdersResponse;

/**
 * The type Order controller.
 * Responsible for handling the order endpoints
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    private final transient AuthManager authManager;
    private final transient OrderService orderService;

    private final transient MailingService mailingService;
    private final transient OrderOperationService orderOperationService;

    /**
     * Instantiates a new Order controller with the needed authentication manager and services
     *
     * @param authManager  the authentication manager
     * @param orderService the order service
     */
    @Autowired
    public OrderController(AuthManager authManager, OrderService orderService, MailingService mailingService,
                           OrderOperationService orderOperationService) {
        this.authManager = authManager;
        this.orderService = orderService;
        this.mailingService = mailingService;
        this.orderOperationService = orderOperationService;
    }

    /**
     * Place an order endpoint, persists the order to the database if valid
     * Includes validation of user and processes order in the order service
     *
     * @param incoming the incoming order
     * @return the response entity
     */
    @PostMapping("/place")
    public ResponseEntity<Order> placeOrder(@RequestBody Order incoming) {
        try {
            return orderOperationService.placeOrder(incoming, authManager.getNetId());
        } catch (Exception e) {
            //return bad request with whatever validation has failed
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header(HttpHeaders.WARNING, e.getMessage()).build();
        }
    }

    /**
     * Edit an order endpoint, updates the order in the database if valid
     * Includes validation of user and processes order in order service
     *
     * @param incoming the incoming order
     * @return the response entity
     */
    @PostMapping("/edit")
    public ResponseEntity<Order> editOrder(@RequestBody Order incoming) {
        try {
            return orderOperationService.editOrder(incoming, authManager.getNetId());
        } catch (Exception e) {
            //return bad request with whatever validation has failed
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header(HttpHeaders.WARNING, e.getMessage()).build();
        }
    }

    /**
     * Delete order endpoint, deletes from the database if valid request
     * Includes user validation and processes order in order service
     *
     * @param deleteModel model containing the order id we want to remove
     * @return the response entity
     */
    @DeleteMapping("/delete")
    @SuppressWarnings("PMD")
    public ResponseEntity<Order> deleteOrder(@RequestBody DeleteModel deleteModel) {
        return orderOperationService.deleteOrder(deleteModel, authManager.getNetId(), authManager.getRole());
    }

    /**
     * List orders endpoint, lists all the orders belonging to a user
     *
     * @return the response entity
     */
    @GetMapping("/list")
    public ResponseEntity<OrdersResponse> listOrders() {
        String userId = authManager.getNetId();
        //get a list of the orders that belong to this user
        List<Order> orders = orderService.listOrders(userId);
        return ResponseEntity.status(HttpStatus.OK).body(new OrdersResponse(orders));
    }

    /**
     * List all orders enpoint, only visible to manager
     * returns a list of all the orders in the system
     *
     * @return the response entity
     */
    @GetMapping("/listAll")
    public ResponseEntity<OrdersResponse> listAllOrders() {
        //get all the orders in the system
        List<Order> orders = orderService.listAllOrders();
        return ResponseEntity.status(HttpStatus.OK).body(new OrdersResponse(orders));
    }
}
