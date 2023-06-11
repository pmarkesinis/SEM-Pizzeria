package pizzeria.order.models;

import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
public class CouponModel {

    String id;
    double percentage;
    String type;

}
