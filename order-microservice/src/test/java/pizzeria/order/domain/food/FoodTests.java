package pizzeria.order.domain.food;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FoodTests {
    @Test
    public void foodsEqual_worksCorrectly() {
        Food firstFood = new Food(1, 3, 4, List.of(), List.of());
        Food secondFood = new Food(1, 3, 4, List.of(), List.of());
        assertThat(firstFood.equals(secondFood)).isTrue();
    }

    @Test
    public void foodsEqual_withObject() {
        Food firstFood = new Food(1, 3, 4, List.of(), List.of());
        Object secondFood = new Food(1, 3, 4, List.of(), List.of());

        assertThat(firstFood.equals(secondFood)).isTrue();
    }

    @Test
    public void foodsEqual_withDifferentObject() {
        Food firstFood = new Food(1, 3, 4, List.of(), List.of());
        String secondFood = "Random String";
        assertThat(firstFood.equals(secondFood)).isFalse();
    }

    @Test
    public void foodsEqual_areDifferent() {
        Food firstFood = new Food(1, 3, 4, List.of(), List.of());
        Food secondFood = new Food(3, 3, 4, List.of(), List.of());
        assertThat(firstFood.equals(secondFood)).isFalse();
    }

    @Test
    public void foodsHasCode_worksCorrectly() {
        Food firstFood = new Food(12321, 3, 4, List.of(), List.of());
        Food secondFood = new Food(12321, 3312312, 4512, List.of(12L), List.of(3L));
        assertThat(firstFood.hashCode()).isEqualTo(secondFood.hashCode());
    }
    @Test
    public void foodsHasCode_isDifferent() {
        Food firstFood = new Food(12321, 3, 4, List.of(), List.of());
        Food secondFood = new Food(12321221, 3312312, 4512, List.of(12L), List.of(3L));
        assertThat(firstFood.hashCode()).isNotEqualTo(secondFood.hashCode());
    }
}
