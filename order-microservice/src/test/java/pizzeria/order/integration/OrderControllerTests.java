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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.client.RestTemplate;
import pizzeria.order.authentication.AuthManager;
import pizzeria.order.authentication.JwtTokenVerifier;
import pizzeria.order.domain.coupon.Coupon_2for1_Repository;
import pizzeria.order.domain.coupon.Coupon_percentage_Repository;
import pizzeria.order.domain.coupon.PercentageCoupon;
import pizzeria.order.domain.coupon.TwoForOneCoupon;
import pizzeria.order.domain.food.Food;
import pizzeria.order.domain.food.FoodPriceService;
import pizzeria.order.domain.food.FoodRepository;
import pizzeria.order.domain.mailing.MailingService;
import pizzeria.order.domain.order.ClockWrapper;
import pizzeria.order.domain.order.Order;
import pizzeria.order.domain.order.OrderRepository;
import pizzeria.order.domain.store.Store;
import pizzeria.order.domain.store.StoreService;
import pizzeria.order.integration.utils.JsonUtil;
import pizzeria.order.models.*;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockTokenVerifier", "mockAuthenticationManager", "restTemplateProfile", "mockMailService", "mockPriceService", "clockWrapper"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureMockMvc
public class OrderControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private transient AuthManager mockAuthManager;

    @Autowired
    private transient JwtTokenVerifier mockJwtTokenVerifier;

    @Autowired
    private transient FoodRepository foodRepository;

    @Autowired
    private transient OrderRepository orderRepository;

    @Autowired
    private transient MailingService mailingService;

    @Autowired
    private transient StoreService storeService;

    @Autowired
    private transient RestTemplate restTemplate;

    @Autowired
    private transient FoodPriceService foodPriceService;

    @Autowired
    private transient ClockWrapper clockWrapper;

    @Autowired
    private transient Coupon_percentage_Repository coupon_percentage_repository;
    @Autowired
    private transient Coupon_2for1_Repository coupon_2for1_repository;

    @BeforeEach
    public void init() {
        when(mockAuthManager.getNetId()).thenReturn("Mocked Id");
        when(mockAuthManager.getRole()).thenReturn("[ROLE_MANAGER]");
        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(true);
        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("Mocked Id");
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn(List.of(new SimpleGrantedAuthority("ROLE_MANAGER")));
        when(clockWrapper.getNow()).thenReturn(LocalDateTime.of(2022, Month.JANUARY, 3, 14, 31, 1));

        try {
            storeService.addStore(new Store("NL-2624ME", "bor@abv.bg"));
        } catch (Exception e) {
            System.out.println("ERROR");
        }

    }

    @Test
    void placeOrder_worksCorrectly() throws Exception {
        Food firstFood = new Food();
        firstFood.setBaseIngredients(List.of(1L));
        firstFood.setExtraIngredients(List.of(4L));
        firstFood.setRecipeId(2L);

        OrderPlaceModel order = new OrderPlaceModel();
        order.setUserId("Mocked Id");
        order.setCouponIds(List.of());
        order.setFoods(List.of(firstFood));
        order.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 2, 1, 3));
        order.setPrice(114.3);
        order.setStoreId(1L);

        Map<Long, Tuple> foodPrices = new HashMap<>();
        Map<Long, Tuple> ingredientPrices = new HashMap<>();

        foodPrices.put(2L, new Tuple(100.0, "MockName1"));
        ingredientPrices.put(1L, new Tuple(13.5, "MockName2"));
        ingredientPrices.put(4L, new Tuple(14.3, "MockName3"));

        GetPricesResponseModel pricesResponseModel = new GetPricesResponseModel();
        pricesResponseModel.setIngredientPrices(ingredientPrices);
        pricesResponseModel.setFoodPrices(foodPrices);

        String serializedString = JsonUtil.serialize(order);

        when(foodPriceService.getFoodPrices(any())).thenReturn(pricesResponseModel);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/order/place")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedString)
                .header("Authorization", "Bearer MockedToken"));

        MvcResult response = resultActions.andExpect(status().isCreated()).andReturn();

        Order actualOrder = JsonUtil.deserialize(response.getResponse().getContentAsString(), Order.class);

        assertThat(actualOrder.getStoreId()).isEqualTo(order.getStoreId());
        assertThat(actualOrder.getUserId()).isEqualTo(order.getUserId());
        assertThat(actualOrder.getPickupTime()).isEqualTo(order.getPickupTime());
        assertThat(actualOrder.getCouponIds()).containsExactlyInAnyOrderElementsOf(order.getCouponIds());
        for (Food currentFood : actualOrder.getFoods()) {
            assertThat(currentFood.getRecipeId()).isEqualTo(foodRepository.findById(currentFood.getId()).get().getRecipeId());
            assertThat(currentFood.getBaseIngredients()).containsExactlyInAnyOrderElementsOf(foodRepository.findById(currentFood.getId()).get().getBaseIngredients());
            assertThat(currentFood.getExtraIngredients()).containsExactlyInAnyOrderElementsOf(foodRepository.findById(currentFood.getId()).get().getExtraIngredients());
        }
    }

    @Test
    void placeOrder_checkCouponFunctionality() throws Exception {
        PercentageCoupon percentageCoupon = new PercentageCoupon("Coupon", 0.2);

        coupon_percentage_repository.save(percentageCoupon);

        Food firstFood = new Food();
        firstFood.setBaseIngredients(List.of(1L));
        firstFood.setExtraIngredients(List.of(4L));
        firstFood.setRecipeId(2L);

        OrderPlaceModel order = new OrderPlaceModel();
        order.setUserId("Mocked Id");
        order.setCouponIds(List.of("Coupon"));
        order.setFoods(List.of(firstFood));
        order.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 2, 1, 3));
        order.setPrice(91.44);
        order.setStoreId(1L);

        Map<Long, Tuple> foodPrices = new HashMap<>();
        Map<Long, Tuple> ingredientPrices = new HashMap<>();

        foodPrices.put(2L, new Tuple(100.0, "MockName1"));
        ingredientPrices.put(1L, new Tuple(13.5, "MockName2"));
        ingredientPrices.put(4L, new Tuple(14.3, "MockName3"));

        GetPricesResponseModel pricesResponseModel = new GetPricesResponseModel();
        pricesResponseModel.setIngredientPrices(ingredientPrices);
        pricesResponseModel.setFoodPrices(foodPrices);

        String serializedString = JsonUtil.serialize(order);

        when(foodPriceService.getFoodPrices(any())).thenReturn(pricesResponseModel);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/order/place")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedString)
                .header("Authorization", "Bearer MockedToken"));

        MvcResult response = resultActions.andExpect(status().isCreated()).andReturn();

        Order actualOrder = JsonUtil.deserialize(response.getResponse().getContentAsString(), Order.class);

        assertThat(actualOrder.getStoreId()).isEqualTo(order.getStoreId());
        assertThat(actualOrder.getUserId()).isEqualTo(order.getUserId());
        assertThat(actualOrder.getPickupTime()).isEqualTo(order.getPickupTime());
        assertThat(actualOrder.getCouponIds()).containsExactlyInAnyOrderElementsOf(order.getCouponIds());
        for (Food currentFood : actualOrder.getFoods()) {
            assertThat(currentFood.getRecipeId()).isEqualTo(foodRepository.findById(currentFood.getId()).get().getRecipeId());
            assertThat(currentFood.getBaseIngredients()).containsExactlyInAnyOrderElementsOf(foodRepository.findById(currentFood.getId()).get().getBaseIngredients());
            assertThat(currentFood.getExtraIngredients()).containsExactlyInAnyOrderElementsOf(foodRepository.findById(currentFood.getId()).get().getExtraIngredients());
        }
    }

    @Test
    void placeOrder_twoCoupons() throws Exception {
        PercentageCoupon percentageCoupon = new PercentageCoupon("Coupon", 0.2);

        coupon_percentage_repository.save(percentageCoupon);

        PercentageCoupon percentageCoupon2 = new PercentageCoupon("Coupon", 0.4);

        coupon_percentage_repository.save(percentageCoupon2);

        Food firstFood = new Food();
        firstFood.setBaseIngredients(List.of(1L));
        firstFood.setExtraIngredients(List.of(4L));
        firstFood.setRecipeId(2L);

        OrderPlaceModel order = new OrderPlaceModel();
        order.setUserId("Mocked Id");
        order.setCouponIds(List.of("Coupon"));
        order.setFoods(List.of(firstFood));
        order.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 2, 1, 3));
        order.setPrice(68.58);
        order.setStoreId(1L);

        Map<Long, Tuple> foodPrices = new HashMap<>();
        Map<Long, Tuple> ingredientPrices = new HashMap<>();

        foodPrices.put(2L, new Tuple(100.0, "MockName1"));
        ingredientPrices.put(1L, new Tuple(13.5, "MockName2"));
        ingredientPrices.put(4L, new Tuple(14.3, "MockName3"));

        GetPricesResponseModel pricesResponseModel = new GetPricesResponseModel();
        pricesResponseModel.setIngredientPrices(ingredientPrices);
        pricesResponseModel.setFoodPrices(foodPrices);

        String serializedString = JsonUtil.serialize(order);

        when(foodPriceService.getFoodPrices(any())).thenReturn(pricesResponseModel);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/order/place")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedString)
                .header("Authorization", "Bearer MockedToken"));

        MvcResult response = resultActions.andExpect(status().isCreated()).andReturn();

        Order actualOrder = JsonUtil.deserialize(response.getResponse().getContentAsString(), Order.class);

        assertThat(actualOrder.getStoreId()).isEqualTo(order.getStoreId());
        assertThat(actualOrder.getUserId()).isEqualTo(order.getUserId());
        assertThat(actualOrder.getPickupTime()).isEqualTo(order.getPickupTime());
        assertThat(actualOrder.getCouponIds()).containsExactlyInAnyOrderElementsOf(order.getCouponIds());
        for (Food currentFood : actualOrder.getFoods()) {
            assertThat(currentFood.getRecipeId()).isEqualTo(foodRepository.findById(currentFood.getId()).get().getRecipeId());
            assertThat(currentFood.getBaseIngredients()).containsExactlyInAnyOrderElementsOf(foodRepository.findById(currentFood.getId()).get().getBaseIngredients());
            assertThat(currentFood.getExtraIngredients()).containsExactlyInAnyOrderElementsOf(foodRepository.findById(currentFood.getId()).get().getExtraIngredients());
        }
    }

    @Test
    void placeOrder_twoForOneCoupon() throws Exception {
        TwoForOneCoupon twoForOneCoupon = new TwoForOneCoupon("Coupon");

        coupon_2for1_repository.save(twoForOneCoupon);

        Food firstFood = new Food();
        firstFood.setBaseIngredients(List.of(1L));
        firstFood.setExtraIngredients(List.of(4L));
        firstFood.setRecipeId(2L);

        Food secondFood = new Food();
        secondFood.setBaseIngredients(List.of(1L));
        secondFood.setExtraIngredients(List.of(4L));
        secondFood.setRecipeId(2L);

        OrderPlaceModel order = new OrderPlaceModel();
        order.setUserId("Mocked Id");
        order.setCouponIds(List.of("Coupon"));
        order.setFoods(List.of(firstFood, secondFood));
        order.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 2, 1, 3));
        order.setPrice(100.0 + 2*14.3);
        order.setStoreId(1L);

        Map<Long, Tuple> foodPrices = new HashMap<>();
        Map<Long, Tuple> ingredientPrices = new HashMap<>();

        foodPrices.put(2L, new Tuple(100.0, "MockName1"));
        ingredientPrices.put(1L, new Tuple(13.5, "MockName2"));
        ingredientPrices.put(4L, new Tuple(14.3, "MockName3"));

        GetPricesResponseModel pricesResponseModel = new GetPricesResponseModel();
        pricesResponseModel.setIngredientPrices(ingredientPrices);
        pricesResponseModel.setFoodPrices(foodPrices);

        String serializedString = JsonUtil.serialize(order);

        when(foodPriceService.getFoodPrices(any())).thenReturn(pricesResponseModel);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/order/place")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedString)
                .header("Authorization", "Bearer MockedToken"));

        MvcResult response = resultActions.andExpect(status().isCreated()).andReturn();

        Order actualOrder = JsonUtil.deserialize(response.getResponse().getContentAsString(), Order.class);

        assertThat(actualOrder.getStoreId()).isEqualTo(order.getStoreId());
        assertThat(actualOrder.getUserId()).isEqualTo(order.getUserId());
        assertThat(actualOrder.getPickupTime()).isEqualTo(order.getPickupTime());
        assertThat(actualOrder.getCouponIds()).containsExactlyInAnyOrderElementsOf(order.getCouponIds());
        for (Food currentFood : actualOrder.getFoods()) {
            assertThat(currentFood.getRecipeId()).isEqualTo(foodRepository.findById(currentFood.getId()).get().getRecipeId());
            assertThat(currentFood.getBaseIngredients()).containsExactlyInAnyOrderElementsOf(foodRepository.findById(currentFood.getId()).get().getBaseIngredients());
            assertThat(currentFood.getExtraIngredients()).containsExactlyInAnyOrderElementsOf(foodRepository.findById(currentFood.getId()).get().getExtraIngredients());
        }
    }

    @Test
    void placeOrder_threeCoupon() throws Exception {
        TwoForOneCoupon twoForOneCoupon = new TwoForOneCoupon("Coupon");

        coupon_2for1_repository.save(twoForOneCoupon);

        PercentageCoupon percentageCoupon = new PercentageCoupon("Coupon2", 0.05);

        coupon_percentage_repository.save(percentageCoupon);

        Food firstFood = new Food();
        firstFood.setBaseIngredients(List.of(1L));
        firstFood.setExtraIngredients(List.of(4L));
        firstFood.setRecipeId(2L);

        Food secondFood = new Food();
        secondFood.setBaseIngredients(List.of(1L));
        secondFood.setExtraIngredients(List.of(4L));
        secondFood.setRecipeId(2L);

        OrderPlaceModel order = new OrderPlaceModel();
        order.setUserId("Mocked Id");
        order.setCouponIds(List.of("Coupon", "Coupon2"));
        order.setFoods(List.of(firstFood, secondFood));
        order.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 2, 1, 3));
        order.setPrice(100.0 + 2*14.3);
        order.setStoreId(1L);

        Map<Long, Tuple> foodPrices = new HashMap<>();
        Map<Long, Tuple> ingredientPrices = new HashMap<>();

        foodPrices.put(2L, new Tuple(100.0, "MockName1"));
        ingredientPrices.put(1L, new Tuple(13.5, "MockName2"));
        ingredientPrices.put(4L, new Tuple(14.3, "MockName3"));

        GetPricesResponseModel pricesResponseModel = new GetPricesResponseModel();
        pricesResponseModel.setIngredientPrices(ingredientPrices);
        pricesResponseModel.setFoodPrices(foodPrices);

        String serializedString = JsonUtil.serialize(order);

        when(foodPriceService.getFoodPrices(any())).thenReturn(pricesResponseModel);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/order/place")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedString)
                .header("Authorization", "Bearer MockedToken"));

        MvcResult response = resultActions.andExpect(status().isCreated()).andReturn();

        Order actualOrder = JsonUtil.deserialize(response.getResponse().getContentAsString(), Order.class);

        assertThat(actualOrder.getStoreId()).isEqualTo(order.getStoreId());
        assertThat(actualOrder.getUserId()).isEqualTo(order.getUserId());
        assertThat(actualOrder.getPickupTime()).isEqualTo(order.getPickupTime());
        assertThat(actualOrder.getCouponIds()).containsExactly(twoForOneCoupon.getId());
        for (Food currentFood : actualOrder.getFoods()) {
            assertThat(currentFood.getRecipeId()).isEqualTo(foodRepository.findById(currentFood.getId()).get().getRecipeId());
            assertThat(currentFood.getBaseIngredients()).containsExactlyInAnyOrderElementsOf(foodRepository.findById(currentFood.getId()).get().getBaseIngredients());
            assertThat(currentFood.getExtraIngredients()).containsExactlyInAnyOrderElementsOf(foodRepository.findById(currentFood.getId()).get().getExtraIngredients());
        }
    }

    @Test
    void placeOrder_invalidStore() throws Exception {
        Food firstFood = new Food();
        firstFood.setBaseIngredients(List.of(1L));
        firstFood.setExtraIngredients(List.of(4L));
        firstFood.setRecipeId(2L);

        OrderPlaceModel order = new OrderPlaceModel();
        order.setUserId("Mocked Id");
        order.setCouponIds(List.of());
        order.setFoods(List.of(firstFood));
        order.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 2, 1, 3));
        order.setPrice(127.8);
        order.setStoreId(2L);

        Map<Long, Tuple> foodPrices = new HashMap<>();
        Map<Long, Tuple> ingredientPrices = new HashMap<>();

        foodPrices.put(2L, new Tuple(100.0, "MockName1"));
        ingredientPrices.put(1L, new Tuple(13.5, "MockName2"));
        ingredientPrices.put(4L, new Tuple(14.3, "MockName3"));

        GetPricesResponseModel pricesResponseModel = new GetPricesResponseModel();
        pricesResponseModel.setIngredientPrices(ingredientPrices);
        pricesResponseModel.setFoodPrices(foodPrices);

        String serializedString = JsonUtil.serialize(order);

        when(foodPriceService.getFoodPrices(any())).thenReturn(pricesResponseModel);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/order/place")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedString)
                .header("Authorization", "Bearer MockedToken"));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void placeOrder_userIsDifferent() throws Exception {
        Food firstFood = new Food();
        firstFood.setBaseIngredients(List.of(1L));
        firstFood.setExtraIngredients(List.of(4L));
        firstFood.setRecipeId(2L);

        OrderPlaceModel order = new OrderPlaceModel();
        order.setUserId("Mocked Id2");
        order.setCouponIds(List.of());
        order.setFoods(List.of(firstFood));
        order.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 2, 1, 3));
        order.setPrice(127.8);
        order.setStoreId(1L);

        Map<Long, Tuple> foodPrices = new HashMap<>();
        Map<Long, Tuple> ingredientPrices = new HashMap<>();

        foodPrices.put(2L, new Tuple(100.0, "MockName1"));
        ingredientPrices.put(1L, new Tuple(13.5, "MockName2"));
        ingredientPrices.put(4L, new Tuple(14.3, "MockName3"));

        GetPricesResponseModel pricesResponseModel = new GetPricesResponseModel();
        pricesResponseModel.setIngredientPrices(ingredientPrices);
        pricesResponseModel.setFoodPrices(foodPrices);

        String serializedString = JsonUtil.serialize(order);

        when(foodPriceService.getFoodPrices(any())).thenReturn(pricesResponseModel);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/order/place")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedString)
                .header("Authorization", "Bearer MockedToken"));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void placeOrder_sumIsDifferent() throws Exception {
        Food firstFood = new Food();
        firstFood.setBaseIngredients(List.of(1L));
        firstFood.setExtraIngredients(List.of(4L));
        firstFood.setRecipeId(2L);

        OrderPlaceModel order = new OrderPlaceModel();
        order.setUserId("Mocked Id");
        order.setCouponIds(List.of());
        order.setFoods(List.of(firstFood));
        order.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 2, 1, 3));
        order.setPrice(127.9);
        order.setStoreId(1L);

        Map<Long, Tuple> foodPrices = new HashMap<>();
        Map<Long, Tuple> ingredientPrices = new HashMap<>();

        foodPrices.put(2L, new Tuple(100.0, "MockName1"));
        ingredientPrices.put(1L, new Tuple(13.5, "MockName2"));
        ingredientPrices.put(4L, new Tuple(14.3, "MockName3"));

        GetPricesResponseModel pricesResponseModel = new GetPricesResponseModel();
        pricesResponseModel.setIngredientPrices(ingredientPrices);
        pricesResponseModel.setFoodPrices(foodPrices);

        String serializedString = JsonUtil.serialize(order);

        when(foodPriceService.getFoodPrices(any())).thenReturn(pricesResponseModel);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/order/place")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedString)
                .header("Authorization", "Bearer MockedToken"));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void placeOrder_timeToChangeHasExpired() throws Exception {
        Food firstFood = new Food();
        firstFood.setBaseIngredients(List.of(1L));
        firstFood.setExtraIngredients(List.of(4L));
        firstFood.setRecipeId(2L);

        OrderPlaceModel order = new OrderPlaceModel();
        order.setUserId("Mocked Id");
        order.setCouponIds(List.of());
        order.setFoods(List.of(firstFood));
        order.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 14, 0, 0));
        order.setPrice(127.8);
        order.setStoreId(1L);

        when(clockWrapper.getNow()).thenReturn(LocalDateTime.of(2023, Month.JANUARY, 3, 14, 31, 1));

        Map<Long, Tuple> foodPrices = new HashMap<>();
        Map<Long, Tuple> ingredientPrices = new HashMap<>();

        foodPrices.put(2L, new Tuple(100.0, "MockName1"));
        ingredientPrices.put(1L, new Tuple(13.5, "MockName2"));
        ingredientPrices.put(4L, new Tuple(14.3, "MockName3"));

        GetPricesResponseModel pricesResponseModel = new GetPricesResponseModel();
        pricesResponseModel.setIngredientPrices(ingredientPrices);
        pricesResponseModel.setFoodPrices(foodPrices);

        String serializedString = JsonUtil.serialize(order);

        when(foodPriceService.getFoodPrices(any())).thenReturn(pricesResponseModel);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/order/place")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedString)
                .header("Authorization", "Bearer MockedToken"));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void placeOrder_nonAuthorised() throws Exception {
        Food firstFood = new Food();
        firstFood.setBaseIngredients(List.of(1L));
        firstFood.setExtraIngredients(List.of(4L));
        firstFood.setRecipeId(2L);

        OrderPlaceModel order = new OrderPlaceModel();
        order.setUserId("Mocked Id");
        order.setCouponIds(List.of());
        order.setFoods(List.of(firstFood));
        order.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 14, 0, 0));
        order.setPrice(127.8);
        order.setStoreId(1L);

        when(clockWrapper.getNow()).thenReturn(LocalDateTime.of(2023, Month.JANUARY, 3, 14, 31, 1));

        Map<Long, Tuple> foodPrices = new HashMap<>();
        Map<Long, Tuple> ingredientPrices = new HashMap<>();

        foodPrices.put(2L, new Tuple(100.0, "MockName1"));
        ingredientPrices.put(1L, new Tuple(13.5, "MockName2"));
        ingredientPrices.put(4L, new Tuple(14.3, "MockName3"));

        GetPricesResponseModel pricesResponseModel = new GetPricesResponseModel();
        pricesResponseModel.setIngredientPrices(ingredientPrices);
        pricesResponseModel.setFoodPrices(foodPrices);

        String serializedString = JsonUtil.serialize(order);

        when(foodPriceService.getFoodPrices(any())).thenReturn(pricesResponseModel);

        when(mockJwtTokenVerifier.validateToken(anyString())).thenReturn(false);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/order/place")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedString)
                .header("Authorization", "Bearer MockedToken"));

        resultActions.andExpect(status().isForbidden());
    }

    @Test
    void editOrder_worksCorrectly() throws Exception {
        Food firstFood = new Food();
        firstFood.setBaseIngredients(List.of(1L));
        firstFood.setExtraIngredients(List.of(4L));
        firstFood.setRecipeId(2L);

        Food secondFood = new Food();
        secondFood.setBaseIngredients(List.of(1L, 10L));
        secondFood.setExtraIngredients(List.of(4L));
        secondFood.setRecipeId(3L);

        Order order = new Order();
        order.setUserId("Mocked Id");
        order.setCouponIds(List.of());
        order.setFoods(List.of(firstFood));
        order.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 14, 0, 0));
        order.setPrice(127.8);
        order.setStoreId(1L);

        orderRepository.save(order);

        OrderEditModel editOrder = new OrderEditModel();
        editOrder.setUserId("Mocked Id");
        editOrder.setCouponIds(List.of());
        editOrder.setFoods(List.of(firstFood, secondFood));
        editOrder.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 14, 0, 0));
        editOrder.setPrice(228.6);
        editOrder.setStoreId(1L);
        editOrder.setOrderId(order.getOrderId());

        Map<Long, Tuple> foodPrices = new HashMap<>();
        Map<Long, Tuple> ingredientPrices = new HashMap<>();

        foodPrices.put(2L, new Tuple(100.0, "MockName1"));
        foodPrices.put(3L, new Tuple(100.0, "MockName5"));
        ingredientPrices.put(1L, new Tuple(13.5, "MockName2"));
        ingredientPrices.put(4L, new Tuple(14.3, "MockName3"));
        ingredientPrices.put(10L, new Tuple(14.3, "MockName4"));

        GetPricesResponseModel pricesResponseModel = new GetPricesResponseModel();
        pricesResponseModel.setIngredientPrices(ingredientPrices);
        pricesResponseModel.setFoodPrices(foodPrices);

        String serializedString = JsonUtil.serialize(editOrder);

        when(foodPriceService.getFoodPrices(any())).thenReturn(pricesResponseModel);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/order/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedString)
                .header("Authorization", "Bearer MockedToken"));

        resultActions.andExpect(status().isCreated());
    }
    @Test
    void editOrder_nonAuthorised() throws Exception {
        Food firstFood = new Food();
        firstFood.setBaseIngredients(List.of(1L));
        firstFood.setExtraIngredients(List.of(4L));
        firstFood.setRecipeId(2L);

        Food secondFood = new Food();
        secondFood.setBaseIngredients(List.of(1L, 10L));
        secondFood.setExtraIngredients(List.of(4L));
        secondFood.setRecipeId(3L);

        Order order = new Order();
        order.setUserId("Mocked Id");
        order.setCouponIds(List.of());
        order.setFoods(List.of(firstFood));
        order.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 14, 0, 0));
        order.setPrice(127.8);
        order.setStoreId(1L);

        orderRepository.save(order);

        OrderEditModel editOrder = new OrderEditModel();
        editOrder.setUserId("Mocked Id2");
        editOrder.setCouponIds(List.of());
        editOrder.setFoods(List.of(firstFood, secondFood));
        editOrder.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 14, 0, 0));
        editOrder.setPrice(269.9);
        editOrder.setStoreId(1L);
        editOrder.setOrderId(order.getOrderId());


        Map<Long, Tuple> foodPrices = new HashMap<>();
        Map<Long, Tuple> ingredientPrices = new HashMap<>();

        foodPrices.put(2L, new Tuple(100.0, "MockName1"));
        foodPrices.put(3L, new Tuple(100.0, "MockName5"));
        ingredientPrices.put(1L, new Tuple(13.5, "MockName2"));
        ingredientPrices.put(4L, new Tuple(14.3, "MockName3"));
        ingredientPrices.put(10L, new Tuple(14.3, "MockName4"));

        GetPricesResponseModel pricesResponseModel = new GetPricesResponseModel();
        pricesResponseModel.setIngredientPrices(ingredientPrices);
        pricesResponseModel.setFoodPrices(foodPrices);

        String serializedString = JsonUtil.serialize(editOrder);

        when(foodPriceService.getFoodPrices(any())).thenReturn(pricesResponseModel);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/order/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedString)
                .header("Authorization", "Bearer MockedToken"));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void editOrder_noSuchStore() throws Exception {
        Food firstFood = new Food();
        firstFood.setBaseIngredients(List.of(1L));
        firstFood.setExtraIngredients(List.of(4L));
        firstFood.setRecipeId(2L);

        Food secondFood = new Food();
        secondFood.setBaseIngredients(List.of(1L, 10L));
        secondFood.setExtraIngredients(List.of(4L));
        secondFood.setRecipeId(3L);

        Order order = new Order();
        order.setUserId("Mocked Id");
        order.setCouponIds(List.of());
        order.setFoods(List.of(firstFood));
        order.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 14, 0, 0));
        order.setPrice(127.8);
        order.setStoreId(1L);

        orderRepository.save(order);

        OrderEditModel editOrder = new OrderEditModel();
        editOrder.setUserId("Mocked Id");
        editOrder.setCouponIds(List.of());
        editOrder.setFoods(List.of(firstFood, secondFood));
        editOrder.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 14, 0, 0));
        editOrder.setPrice(269.9);
        editOrder.setStoreId(2L);
        editOrder.setOrderId(order.getOrderId());

        Map<Long, Tuple> foodPrices = new HashMap<>();
        Map<Long, Tuple> ingredientPrices = new HashMap<>();

        foodPrices.put(2L, new Tuple(100.0, "MockName1"));
        foodPrices.put(3L, new Tuple(100.0, "MockName5"));
        ingredientPrices.put(1L, new Tuple(13.5, "MockName2"));
        ingredientPrices.put(4L, new Tuple(14.3, "MockName3"));
        ingredientPrices.put(10L, new Tuple(14.3, "MockName4"));

        GetPricesResponseModel pricesResponseModel = new GetPricesResponseModel();
        pricesResponseModel.setIngredientPrices(ingredientPrices);
        pricesResponseModel.setFoodPrices(foodPrices);

        String serializedString = JsonUtil.serialize(editOrder);

        when(foodPriceService.getFoodPrices(any())).thenReturn(pricesResponseModel);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/order/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedString)
                .header("Authorization", "Bearer MockedToken"));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void editOrder_invalidEdit() throws Exception {
        Food firstFood = new Food();
        firstFood.setBaseIngredients(List.of(1L));
        firstFood.setExtraIngredients(List.of(4L));
        firstFood.setRecipeId(2L);

        Food secondFood = new Food();
        secondFood.setBaseIngredients(List.of(1L, 10L));
        secondFood.setExtraIngredients(List.of(4L));
        secondFood.setRecipeId(3L);

        Order order = new Order();
        order.setUserId("Mocked Id");
        order.setCouponIds(List.of());
        order.setFoods(List.of(firstFood));
        order.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 14, 0, 0));
        order.setPrice(127.8);
        order.setStoreId(1L);

        orderRepository.save(order);

        when(mockJwtTokenVerifier.getNetIdFromToken(anyString())).thenReturn("Mocked Id2");

        OrderEditModel editOrder = new OrderEditModel();
        editOrder.setUserId("Mocked Id2");
        editOrder.setCouponIds(List.of());
        editOrder.setFoods(List.of(firstFood, secondFood));
        editOrder.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 14, 0, 0));
        editOrder.setPrice(269.9);
        editOrder.setStoreId(1L);
        editOrder.setOrderId(order.getOrderId());


        Map<Long, Tuple> foodPrices = new HashMap<>();
        Map<Long, Tuple> ingredientPrices = new HashMap<>();

        foodPrices.put(2L, new Tuple(100.0, "MockName1"));
        foodPrices.put(3L, new Tuple(100.0, "MockName5"));
        ingredientPrices.put(1L, new Tuple(13.5, "MockName2"));
        ingredientPrices.put(4L, new Tuple(14.3, "MockName3"));
        ingredientPrices.put(10L, new Tuple(14.3, "MockName4"));

        GetPricesResponseModel pricesResponseModel = new GetPricesResponseModel();
        pricesResponseModel.setIngredientPrices(ingredientPrices);
        pricesResponseModel.setFoodPrices(foodPrices);

        String serializedString = JsonUtil.serialize(editOrder);

        when(foodPriceService.getFoodPrices(any())).thenReturn(pricesResponseModel);

        // Act
        ResultActions resultActions = mockMvc.perform(post("/order/edit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedString)
                .header("Authorization", "Bearer MockedToken"));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void deleteOrder_worksCorrectly() throws Exception {
        Food firstFood = new Food();
        firstFood.setBaseIngredients(List.of(1L));
        firstFood.setExtraIngredients(List.of(4L));
        firstFood.setRecipeId(2L);


        Order order = new Order();
        order.setUserId("Mocked Id");
        order.setCouponIds(List.of());
        order.setFoods(List.of(firstFood));
        order.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 14, 0, 0));
        order.setPrice(127.8);
        order.setStoreId(1L);

        orderRepository.save(order);

        Long orderId = order.getOrderId();

        DeleteModel deleteModel = new DeleteModel();
        deleteModel.setOrderId(order.getOrderId());

        String serializedString = JsonUtil.serialize(deleteModel);

        // Act
        ResultActions resultActions = mockMvc.perform(delete("/order/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedString)
                .header("Authorization", "Bearer MockedToken"));

        resultActions.andExpect(status().isOk());

        assertThat(orderRepository.findByOrderId(orderId)).isEmpty();
    }

    @Test
    void deleteOrder_noSuchOrder() throws Exception {
        Food firstFood = new Food();
        firstFood.setBaseIngredients(List.of(1L));
        firstFood.setExtraIngredients(List.of(4L));
        firstFood.setRecipeId(2L);

        Order order = new Order();
        order.setUserId("Mocked Id");
        order.setCouponIds(List.of());
        order.setFoods(List.of(firstFood));
        order.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 14, 0, 0));
        order.setPrice(127.8);
        order.setStoreId(1L);

        orderRepository.save(order);

        DeleteModel deleteModel = new DeleteModel();
        deleteModel.setOrderId(100L);

        String serializedString = JsonUtil.serialize(deleteModel);

        // Act
        ResultActions resultActions = mockMvc.perform(delete("/order/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedString)
                .header("Authorization", "Bearer MockedToken"));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void deleteOrder_isAManager() throws Exception {
        Food firstFood = new Food();
        firstFood.setBaseIngredients(List.of(1L));
        firstFood.setExtraIngredients(List.of(4L));
        firstFood.setRecipeId(2L);

        Order order = new Order();
        order.setUserId("Mocked Id2");
        order.setCouponIds(List.of());
        order.setFoods(List.of(firstFood));
        order.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 14, 0, 0));
        order.setPrice(127.8);
        order.setStoreId(1L);

        orderRepository.save(order);

        DeleteModel deleteModel = new DeleteModel();
        deleteModel.setOrderId(order.getOrderId());

        String serializedString = JsonUtil.serialize(deleteModel);

        // Act
        ResultActions resultActions = mockMvc.perform(delete("/order/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedString)
                .header("Authorization", "Bearer MockedToken"));

        resultActions.andExpect(status().isOk());
    }

    @Test
    void deleteOrder_isACustomer() throws Exception {
        Food firstFood = new Food();
        firstFood.setBaseIngredients(List.of(1L));
        firstFood.setExtraIngredients(List.of(4L));
        firstFood.setRecipeId(2L);

        Order order = new Order();
        order.setUserId("Mocked Id");
        order.setCouponIds(List.of());
        order.setFoods(List.of(firstFood));
        order.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 14, 0, 0));
        order.setPrice(127.8);
        order.setStoreId(1L);

        orderRepository.save(order);

        when(mockAuthManager.getRole()).thenReturn("ROLE_CUSTOMER");
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn(List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));

        DeleteModel deleteModel = new DeleteModel();
        deleteModel.setOrderId(order.getOrderId());

        String serializedString = JsonUtil.serialize(deleteModel);

        // Act
        ResultActions resultActions = mockMvc.perform(delete("/order/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedString)
                .header("Authorization", "Bearer MockedToken"));

        resultActions.andExpect(status().isOk());
    }

    @Test
    void deleteOrder_cantDelete() throws Exception {
        Food firstFood = new Food();
        firstFood.setBaseIngredients(List.of(1L));
        firstFood.setExtraIngredients(List.of(4L));
        firstFood.setRecipeId(2L);

        Order order = new Order();
        order.setUserId("Mocked Id2");
        order.setCouponIds(List.of());
        order.setFoods(List.of(firstFood));
        order.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 14, 0, 0));
        order.setPrice(127.8);
        order.setStoreId(1L);

        orderRepository.save(order);

        when(mockAuthManager.getRole()).thenReturn("ROLE_CUSTOMER");
        when(mockJwtTokenVerifier.getRoleFromToken(anyString())).thenReturn(List.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));

        DeleteModel deleteModel = new DeleteModel();
        deleteModel.setOrderId(order.getOrderId());

        String serializedString = JsonUtil.serialize(deleteModel);

        // Act
        ResultActions resultActions = mockMvc.perform(delete("/order/delete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(serializedString)
                .header("Authorization", "Bearer MockedToken"));

        resultActions.andExpect(status().isBadRequest());
    }

    @Test
    void listOrders() throws Exception {
        Food firstFood = new Food();
        firstFood.setBaseIngredients(List.of(1L));
        firstFood.setExtraIngredients(List.of(4L));
        firstFood.setRecipeId(2L);
        Order order = new Order();
        order.setUserId("Mocked Id");
        order.setCouponIds(List.of());
        order.setFoods(List.of(firstFood));
        order.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 14, 0, 0));
        order.setPrice(127.8);
        order.setStoreId(1L);
        orderRepository.save(order);

        Food secondFood = new Food();
        secondFood.setBaseIngredients(List.of(1L));
        secondFood.setExtraIngredients(List.of(4L));
        secondFood.setRecipeId(2L);
        Order order2 = new Order(); order2.setUserId("Mocked Id"); order2.setCouponIds(List.of()); order2.setFoods(List.of(secondFood));
        order2.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 14, 0, 0));
        order2.setPrice(127.8); order2.setStoreId(1L);
        orderRepository.save(order2);

        Food thirdFood = new Food();
        thirdFood.setBaseIngredients(List.of(1L));
        thirdFood.setExtraIngredients(List.of(4L));
        thirdFood.setRecipeId(2L);
        Order order3 = new Order();
        order3.setUserId("Mocked Id"); order3.setCouponIds(List.of()); order3.setFoods(List.of(thirdFood));
        order3.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 14, 0, 0)); order3.setPrice(127.8); order3.setStoreId(1L);
        orderRepository.save(order3);

        Food fourthFood = new Food();
        fourthFood.setBaseIngredients(List.of(1L));
        fourthFood.setExtraIngredients(List.of(4L));
        fourthFood.setRecipeId(2L);
        Order notOurFood = new Order();
        notOurFood.setUserId("Mocked Id2");
        notOurFood.setCouponIds(List.of());
        notOurFood.setFoods(List.of(fourthFood));
        notOurFood.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 14, 0, 0));
        notOurFood.setPrice(127.8);
        notOurFood.setStoreId(1L);
        orderRepository.save(notOurFood);

        // Act
        ResultActions resultActions = mockMvc.perform(get("/order/list")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        MvcResult response = resultActions.andExpect(status().isOk()).andReturn();

        OrdersResponse orders = JsonUtil.deserialize(response.getResponse().getContentAsString(), OrdersResponse.class);
        assertThat(orders.getOrders().size() == 3).isTrue();
        for (Order currentOrder : orders.getOrders()) {
            assertThat(currentOrder.getPrice()).isEqualTo(orderRepository.findByOrderId(currentOrder.getOrderId()).get().getPrice());
            assertThat(currentOrder.getStoreId()).isEqualTo(orderRepository.findByOrderId(currentOrder.getOrderId()).get().getStoreId());
            assertThat(currentOrder.getUserId()).isEqualTo(orderRepository.findByOrderId(currentOrder.getOrderId()).get().getUserId());
            assertThat(currentOrder.getFoods()).containsExactlyInAnyOrderElementsOf(orderRepository.findByOrderId(currentOrder.getOrderId()).get().getFoods());
        }
    }

    @Test
    void listOrders_allUsers() throws Exception {
        Food firstFood = new Food();
        firstFood.setBaseIngredients(List.of(1L));
        firstFood.setExtraIngredients(List.of(4L));
        firstFood.setRecipeId(2L);

        Order order = new Order();
        order.setUserId("Mocked Id");
        order.setCouponIds(List.of());
        order.setFoods(List.of(firstFood));
        order.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 14, 0, 0));
        order.setPrice(127.8);
        order.setStoreId(1L);

        orderRepository.save(order);

        Food secondFood = new Food();
        secondFood.setBaseIngredients(List.of(1L));
        secondFood.setExtraIngredients(List.of(4L));
        secondFood.setRecipeId(2L);


        Order order2 = new Order();
        order2.setUserId("Mocked Id"); order2.setCouponIds(List.of()); order2.setFoods(List.of(secondFood));
        order2.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 14, 0, 0)); order2.setPrice(127.8); order2.setStoreId(1L);
        orderRepository.save(order2);

        Food thirdFood = new Food();
        thirdFood.setBaseIngredients(List.of(1L));
        thirdFood.setExtraIngredients(List.of(4L));
        thirdFood.setRecipeId(2L);

        Order order3 = new Order();
        order3.setUserId("Mocked Id"); order3.setCouponIds(List.of()); order3.setFoods(List.of(thirdFood));
        order3.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 14, 0, 0));
        order3.setPrice(127.8); order3.setStoreId(1L);
        orderRepository.save(order3);

        Food fourthFood = new Food();
        fourthFood.setBaseIngredients(List.of(1L));
        fourthFood.setExtraIngredients(List.of(4L));
        fourthFood.setRecipeId(2L);


        Order notOurFood = new Order();
        notOurFood.setUserId("Mocked Id2");
        notOurFood.setCouponIds(List.of());
        notOurFood.setFoods(List.of(fourthFood));
        notOurFood.setPickupTime(LocalDateTime.of(2023, Month.JANUARY, 3, 14, 0, 0));
        notOurFood.setPrice(127.8);
        notOurFood.setStoreId(1L);

        orderRepository.save(notOurFood);

        // Act
        ResultActions resultActions = mockMvc.perform(get("/order/listAll")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer MockedToken"));

        MvcResult response = resultActions.andExpect(status().isOk()).andReturn();

        OrdersResponse orders = JsonUtil.deserialize(response.getResponse().getContentAsString(), OrdersResponse.class);

        assertThat(orders.getOrders().size() == 4).isTrue();

        for (Order currentOrder : orders.getOrders()) {
            assertThat(currentOrder.getPrice()).isEqualTo(orderRepository.findByOrderId(currentOrder.getOrderId()).get().getPrice());
            assertThat(currentOrder.getStoreId()).isEqualTo(orderRepository.findByOrderId(currentOrder.getOrderId()).get().getStoreId());
            assertThat(currentOrder.getUserId()).isEqualTo(orderRepository.findByOrderId(currentOrder.getOrderId()).get().getUserId());
            assertThat(currentOrder.getFoods()).containsExactlyInAnyOrderElementsOf(orderRepository.findByOrderId(currentOrder.getOrderId()).get().getFoods());
        }
    }
}
