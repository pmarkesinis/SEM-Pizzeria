package pizzeria.order.domain.order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pizzeria.order.domain.food.Food;
import pizzeria.order.models.GetPricesResponseModel;
import pizzeria.order.models.Tuple;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(SpringExtension.class)
@SpringBootTest
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class OrderTests {
    @Test
    public void calculatePriceTest(){
        //test to kill a mutant in the calculate price method
        Order order = new Order(2L, List.of(), 3L, "Mocked Id", LocalDateTime.now(), 100.0, List.of());
        //food id 1, with recipe id 1, order id 2, no base ingredient and one extra ingredient with id 1
        Food food = new Food(1L, 1L, 2L, List.of(), List.of(1L));
        //update the foods in the order
        order.setFoods(List.of(food));
        //do the setup of the type of prices response model we would expect (from the food ms)
        Tuple recipe1 = new Tuple(8.0, "dummy_recipe");
        Tuple ingredient1 = new Tuple(2.0, "dummy_ingredient");
        Map<Long, Tuple> recipePrices = new HashMap<>();
        Map<Long, Tuple> ingredientPrices = new HashMap<>();
        recipePrices.put(1L, recipe1);
        ingredientPrices.put(1L, ingredient1);
        GetPricesResponseModel rm = new GetPricesResponseModel(recipePrices, ingredientPrices);

        //assert the price
        assertThat(order.calculatePrice(rm, List.of())).isEqualTo(10.0);
    }

    @Test
    public void orderEquals_worksCorrectly() {
        Order order = new Order(2L, List.of(), 3L, "Mocked Id", LocalDateTime.now(), 100.0, List.of());
        Order secondOrder = new Order(2L, List.of(), 6L, "Mocked Id", LocalDateTime.now(), 100.0, List.of());

        assertThat(order.equals(secondOrder)).isTrue();
    }
    @Test
    public void orderEquals_isNotEqual() {
        Order order = new Order(2L, List.of(), 3L, "Mocked Id", LocalDateTime.now(), 100.0, List.of());
        Order secondOrder = new Order(5L, List.of(), 3L, "Mocked Id", LocalDateTime.now(), 100.0, List.of());

        assertThat(order.equals(secondOrder)).isFalse();
    }

    @Test
    public void orderEquals_isEqualObject() {
        Order order = new Order(2L, List.of(), 3L, "Mocked Id", LocalDateTime.now(), 100.0, List.of());
        Object secondOrder = new Order(2L, List.of(), 3L, "Mocked Id", LocalDateTime.now(), 100.0, List.of());

        assertThat(order.equals(secondOrder)).isTrue();
    }

    @Test
    public void orderEquals_isEqualToItself() {
        Order order = new Order(2L, List.of(), 3L, "Mocked Id", LocalDateTime.now(), 100.0, List.of());

        assertThat(order.equals(order)).isTrue();
    }

    @Test
    public void orderEquals_otherObject() {
        Order order = new Order(2L, List.of(), 3L, "Mocked Id", LocalDateTime.now(), 100.0, List.of());
        Object otherObject = "Random String";

        assertThat(order.equals(otherObject)).isFalse();
    }

    @Test
    public void orderEquals_hashCode() {
        Order order = new Order(2L, List.of(), 3L, "Mocked Id", LocalDateTime.now(), 100.0, List.of());
        Order order2 = new Order(2L, List.of(), 3L, "Mocked Id", LocalDateTime.now(), 100.0, List.of());

        assertThat(order.hashCode()).isEqualTo(order2.hashCode());
    }

    @Test
    public void orderEquals_hashCodeNotEqual() {
        Order order = new Order(2L, List.of(), 3L, "Mocked Id", LocalDateTime.now(), 100.0, List.of());
        Order order2 = new Order(4L, List.of(), 3L, "Mocked Id", LocalDateTime.now(), 100.0, List.of());

        assertThat(order.hashCode()).isNotEqualTo(order2.hashCode());
    }
}
