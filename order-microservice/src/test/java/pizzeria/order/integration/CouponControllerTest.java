package pizzeria.order.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import pizzeria.order.authentication.AuthManager;
import pizzeria.order.authentication.JwtTokenVerifier;
import pizzeria.order.domain.coupon.Coupon_2for1_Repository;
import pizzeria.order.domain.coupon.Coupon_percentage_Repository;
import pizzeria.order.domain.coupon.PercentageCoupon;
import pizzeria.order.domain.coupon.TwoForOneCoupon;
import pizzeria.order.integration.utils.JsonUtil;
import pizzeria.order.models.CouponModel;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles({"mockAuthenticationManager", "mockTokenVerifier"})
@AutoConfigureMockMvc
class CouponControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient Coupon_percentage_Repository coupon_percentage_Repository;

    @Autowired
    private transient Coupon_2for1_Repository coupon_2for1_Repository;

    @Autowired
    private transient AuthManager mockAuthManager;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @BeforeEach
    public void init() {
        when(mockAuthManager.getNetId()).thenReturn("ExampleUser");
        when(mockAuthManager.getRole()).thenReturn("ROLE_MANAGER");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("ExampleUser");
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn(List.of(new SimpleGrantedAuthority("ROLE_MANAGER")));
    }

    @Test
    public void createPercentageCouponSuccessfully() throws Exception {

        final String id = "COUPON";
        final double percentage = 0.2;
        final String type = "PERCENTAGE";

        CouponModel couponModel = new CouponModel();
        couponModel.setPercentage(percentage);
        couponModel.setId(id);
        couponModel.setType(type);
        ResultActions resultActions = mockMvc.perform(post("/coupon/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(couponModel))
                .header("Authorization", "Bearer MockedToken"));


        // assert that it was build correctly
        resultActions.andExpect(status().isCreated());

        //assert that the right coupon was saved
        PercentageCoupon newSavedCoupon = (PercentageCoupon) coupon_percentage_Repository.findById(id).orElseThrow();
        assertEquals(newSavedCoupon.getId(), id);
        assertEquals(newSavedCoupon.getPercentage(), percentage);
    }

    //the percentage here is above 1
    @Test
    public void createCouponWithIncorrectPercentageAboveOne() throws Exception {

        final String id = "COUPON";
        final double percentage = 1.4;
        final String type = "PERCENTAGE";

        CouponModel couponModel = new CouponModel();
        couponModel.setPercentage(percentage);
        couponModel.setId(id);
        couponModel.setType(type);

        ResultActions resultActions = mockMvc.perform(post("/coupon/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(couponModel))
                .header("Authorization", "Bearer MockedToken"));

        //assert that it returns error 400
        resultActions.andExpect(status().isBadRequest());
    }

    //the percentage here is below 0
    @Test
    public void createCouponWithIncorrectPercentageBelowZero() throws Exception {

        final String id = "COUPON";
        final double percentage = -0.7;
        final String type = "PERCENTAGE";

        CouponModel couponModel = new CouponModel();
        couponModel.setPercentage(percentage);
        couponModel.setId(id);
        couponModel.setType(type);

        ResultActions resultActions = mockMvc.perform(post("/coupon/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(couponModel))
                .header("Authorization", "Bearer MockedToken"));

        //assert that it returns error 400
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void createCouponCheckingBoundariesForPercentage() throws Exception {

        final String id = "COUPON";
        final double percentage = 0;
        final String type = "PERCENTAGE";

        CouponModel couponModel = new CouponModel();
        couponModel.setPercentage(percentage);
        couponModel.setId(id);
        couponModel.setType(type);

        ResultActions resultActions = mockMvc.perform(post("/coupon/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(couponModel))
                .header("Authorization", "Bearer MockedToken"));

        //assert that it return 201
        resultActions.andExpect(status().isCreated());

        //assert the coupons details were saved correctly
        PercentageCoupon newSavedCoupon = coupon_percentage_Repository.findById(id).orElseThrow();
        assertEquals(newSavedCoupon.getId(), id);
        assertEquals(newSavedCoupon.getPercentage(), percentage);


        final String id2 = "COUPON2";
        final double percentage2 = 1.0;
        final String type2 = "PERCENTAGE";

        CouponModel cm = new CouponModel();
        cm.setId(id2);
        cm.setPercentage(percentage2);
        cm.setType(type2);

        ResultActions resultActions2 = mockMvc.perform(post("/coupon/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(cm))
                .header("Authorization", "Bearer MockedToken"));

        //assert that it return 201
        resultActions2.andExpect(status().isCreated());
    }

    @Test
    public void updateCouponPercentageCorrectly() throws Exception {
        final String id = "COUPON";
        final double percentage = 0.75;
        final String type = "PERCENTAGE";

        CouponModel couponModel = new CouponModel();
        couponModel.setPercentage(percentage);
        couponModel.setId(id);
        couponModel.setType(type);

        ResultActions resultActions = mockMvc.perform(post("/coupon/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(couponModel))
                .header("Authorization", "Bearer MockedToken"));

        //assert that it return 201
        resultActions.andExpect(status().isCreated());

        //assert the coupons details were saved correctly
        PercentageCoupon newSavedCoupon = coupon_percentage_Repository.findById(id).orElseThrow();
        assertEquals(newSavedCoupon.getId(), id);
        assertEquals(newSavedCoupon.getPercentage(), percentage);


        final double percentage2 = 0.4;

        CouponModel cm = new CouponModel();
        cm.setId(id);
        cm.setPercentage(percentage2);
        cm.setType(type);

        ResultActions resultActions2 = mockMvc.perform(post("/coupon/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(cm))
                .header("Authorization", "Bearer MockedToken"));

        //assert that it return 201
        resultActions2.andExpect(status().isCreated());

        PercentageCoupon newSavedCoupon2 = coupon_percentage_Repository.findById(id).orElseThrow();
        //assert that the percentage was updated
        assertEquals(newSavedCoupon2.getPercentage(), percentage2);
    }

    @Test
    public void createTwoForOneCouponCorrectly() throws Exception {
        final String id = "COUPON";
        final String type = "TWO_FOR_ONE";

        CouponModel couponModel = new CouponModel();
        couponModel.setId(id);
        couponModel.setType(type);

        ResultActions resultActions = mockMvc.perform(post("/coupon/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(couponModel))
                .header("Authorization", "Bearer MockedToken"));

        //assert it returns 201
        resultActions.andExpect(status().isCreated());

        // assert the coupon's details were saved correctly
        TwoForOneCoupon tfo = coupon_2for1_Repository.findById(id).orElseThrow();
        assertEquals(tfo.getId(), id);
    }

    @Test
    public void checkThatWrongTypeOfCouponIsABadRequest() throws Exception {
        final String id = "COUPON";
        final String type = "FREE_MEAL";

        CouponModel couponModel = new CouponModel();
        couponModel.setId(id);
        couponModel.setType(type);

        ResultActions resultActions = mockMvc.perform(post("/coupon/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(couponModel))
                .header("Authorization", "Bearer MockedToken"));

        //assert it returns 201
        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    public void createPercentageCouponWhenNoAuthority() throws Exception {
        final String id = "COUPON";
        final double percentage = 0.2;
        final String type = "PERCENTAGE";

        when(mockAuthManager.getRole()).thenReturn("ROLE_CUSTOMER");
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn(List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));

        CouponModel couponModel = new CouponModel();
        couponModel.setPercentage(percentage);
        couponModel.setId(id);
        couponModel.setType(type);
        ResultActions resultActions = mockMvc.perform(post("/coupon/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(couponModel))
                .header("Authorization", "Bearer MockedToken"));


        //assert the access is forbidden
        resultActions.andExpect(status().isForbidden());
    }

    @Test
    public void createTwoForOneCouponWhenNoAuthority() throws Exception {
        final String id = "COUPON";
        final String type = "TWO_FOR_ONE";

        when(mockAuthManager.getRole()).thenReturn("ROLE_CUSTOMER");
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn(List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));

        CouponModel couponModel = new CouponModel();
        couponModel.setId(id);
        couponModel.setType(type);
        ResultActions resultActions = mockMvc.perform(post("/coupon/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JsonUtil.serialize(couponModel))
                .header("Authorization", "Bearer MockedToken"));


        //assert the access is forbidden
        resultActions.andExpect(status().isForbidden());
    }

}