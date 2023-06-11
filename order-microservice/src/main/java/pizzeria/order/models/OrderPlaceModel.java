package pizzeria.order.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import pizzeria.order.domain.food.Food;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderPlaceModel {
    List<Food> foods;
    Long storeId;
    String userId;
    @JsonFormat
    (shape = JsonFormat.Shape.STRING, pattern = "yyy-MM-dd HH:mm:ss")
    LocalDateTime pickupTime;
    double price;
    List <String> couponIds;
}
