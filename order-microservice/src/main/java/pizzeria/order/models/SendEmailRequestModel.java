package pizzeria.order.models;

import lombok.Data;
import pizzeria.order.domain.order.Order;

@Data
public class SendEmailRequestModel {
    private Long store;
    private String notificationType;
    private Order order;
}
