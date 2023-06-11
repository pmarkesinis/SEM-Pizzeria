package pizzeria.order.domain.food;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;
import pizzeria.order.domain.order.Order;
import pizzeria.order.models.GetPricesResponseModel;
import pizzeria.order.models.Tuple;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FoodPriceServiceTests {
    @ParameterizedTest
    @MethodSource("getFoodPriceSuite")
    void getFoodPrice_worksCorrectly(GetPricesResponseModel model, GetPricesResponseModel expected) {
        //make a dummy order
        Order order = new Order(1L, List.of(new Food(1, 3, 4, List.of(), List.of())), 3L, "Mocked id", LocalDateTime.now(), 134.0, List.of());

        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);

        FoodPriceService foodPriceService = new FoodPriceService(restTemplate);


        when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(ResponseEntity.ok().body(model));

        GetPricesResponseModel actualModel = foodPriceService.getFoodPrices(order);

        verify(restTemplate, times(1)).postForEntity(anyString(), any(), any());

        assertThat(actualModel).isEqualTo(expected);
    }

    static Stream<Arguments> getFoodPriceSuite() {
        GetPricesResponseModel model1 = new GetPricesResponseModel();
        model1.setFoodPrices(Map.of(1L, new Tuple(12.0, "DAS")));
        model1.setIngredientPrices(Map.of(3L, new Tuple(12.0, "DAS2")));

        GetPricesResponseModel model2 = new GetPricesResponseModel();
        model2.setFoodPrices(null);
        model2.setIngredientPrices(Map.of(3L, new Tuple(12.0, "DAS2")));

        GetPricesResponseModel model3 = new GetPricesResponseModel();
        model3.setFoodPrices(Map.of(1L, new Tuple(12.0, "DAS")));
        model3.setIngredientPrices(null);

        return Stream.of(
          Arguments.of(model1, model1),
          //suites that catch the mutation in the extract price response model method
          Arguments.of(model2, new GetPricesResponseModel(new HashMap<>(), model2.getIngredientPrices())),
          Arguments.of(model3, new GetPricesResponseModel(model3.getFoodPrices(), new HashMap<>()))
        );
    }

    @Test
    void getFoodPrice_notAGoodRequest() {
        Order order = new Order(1L, List.of(new Food(1, 3, 4, List.of(), List.of())), 3L, "Mocked id", LocalDateTime.now(), 134.0, List.of());

        RestTemplate restTemplate = Mockito.mock(RestTemplate.class);

        FoodPriceService foodPriceService = new FoodPriceService(restTemplate);

        GetPricesResponseModel model = new GetPricesResponseModel();
        model.setFoodPrices(Map.of(1L, new Tuple(12.0, "DAS")));
        model.setIngredientPrices(Map.of(3L, new Tuple(12.0, "DAS2")));

        when(restTemplate.postForEntity(anyString(), any(), any())).thenReturn(ResponseEntity.badRequest().build());

        GetPricesResponseModel actualModel = foodPriceService.getFoodPrices(order);

        verify(restTemplate, times(1)).postForEntity(anyString(), any(), any());

        assertThat(actualModel).isEqualTo(null);
    }
}
