package pizzeria.order.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pizzeria.order.domain.coupon.CouponService;
import pizzeria.order.models.CouponModel;

/**
 * The type Coupon controller
 * Responsible for handling endpoints related to the coupons in the order microservice
 */
@RestController
@RequestMapping("/coupon")
public class CouponController {
    private final transient CouponService couponService;

    /**
     * Instantiates a new Coupon controller with the coupon service.
     *
     * @param couponService the coupon service
     */
    @Autowired
    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    /**
     * Create coupon response entity.
     *
     * @param coupon the coupon
     * @return the response entity
     */
    @PostMapping("/create")
    public ResponseEntity<Void> createCoupon(@RequestBody CouponModel coupon) {
        //if the coupon created has been successful respond with created
        if (couponService.createCoupon(coupon))
            return ResponseEntity.status(HttpStatus.CREATED).build();
        //else bad request if any errors
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }
}