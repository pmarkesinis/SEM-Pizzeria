package pizzeria.order.domain.coupon;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import pizzeria.order.domain.order.Order;
import pizzeria.order.models.GetPricesResponseModel;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@SpringBootTest
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CouponTests {
    @Test
    public void testThrowsIllegal(){
        //test that we cannot construct illegal percentage coupons
        assertThatThrownBy(() -> {
            PercentageCoupon couponOne = new PercentageCoupon("TestCoupon", 1.5);
        }).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void coupon_equalsWorksCorrectly() {
        PercentageCoupon couponOne = new PercentageCoupon("TestCoupon", 0.2);
        PercentageCoupon couponTwo = new PercentageCoupon("TestCoupon", 0.2);

        assertThat(couponOne.equals(couponTwo)).isTrue();
    }

    @Test
    public void coupon_equalsDifferentObjects() {
        PercentageCoupon couponOne = new PercentageCoupon("TestCoupon", 0.2);
        Object otherObject = "Random string";

        assertThat(couponOne.equals(otherObject)).isFalse();
    }

    @Test
    public void coupon_sameObjects() {
        PercentageCoupon couponOne = new PercentageCoupon("TestCoupon", 0.2);

        assertThat(couponOne.equals(couponOne)).isTrue();
    }

    @Test
    public void coupon_notEqualsWorksCorrectly() {
        PercentageCoupon couponOne = new PercentageCoupon("TestCoupon", 0.2);
        PercentageCoupon couponTwo = new PercentageCoupon("TestCoupon2", 0.2);

        assertThat(couponOne.equals(couponTwo)).isFalse();
    }

    @Test
    public void coupon_ObjectTest() {
        PercentageCoupon couponOne = new PercentageCoupon("TestCoupon", 0.2);
        Object couponTwo = new PercentageCoupon("TestCoupon", 0.2);

        assertThat(couponOne.equals(couponTwo)).isTrue();
    }

    @Test
    public void coupon_hashCodeTest() {
        PercentageCoupon couponOne = new PercentageCoupon("TestCoupon", 0.2);
        Object couponTwo = new PercentageCoupon("TestCoupon", 0.2);

        assertThat(couponOne.hashCode()).isEqualTo(couponTwo.hashCode());
    }

    @Test
    public void coupon_hashCodeTestDifferent() {
        PercentageCoupon couponOne = new PercentageCoupon("TestCoupon", 0.2);
        Object couponTwo = new PercentageCoupon("TestCoupon2", 0.2);

        assertThat(couponOne.hashCode()).isNotEqualTo(couponTwo.hashCode());
    }

    @ParameterizedTest
    @MethodSource("couponTestSuite")
    void percentageCouponTest(double percentage, double basePrice, double expectedPrice) {
        PercentageCoupon couponOne = new PercentageCoupon("TestCoupon", percentage);

        double actualPrice = couponOne.calculatePrice(new Order(), new GetPricesResponseModel(), basePrice);

        assertThat(actualPrice).isEqualTo(expectedPrice);
    }

    static Stream<Arguments> couponTestSuite() {
        return Stream.of(
          Arguments.of(1.0, 100, 0.0),
          Arguments.of(0, 100, 100),
          Arguments.of(0.5, 100, 50)
        );
    }
}
