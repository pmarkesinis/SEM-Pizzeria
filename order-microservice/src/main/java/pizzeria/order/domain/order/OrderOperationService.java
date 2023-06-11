package pizzeria.order.domain.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import pizzeria.order.domain.mailing.MailingService;
import pizzeria.order.domain.store.StoreService;
import pizzeria.order.models.DeleteModel;

import java.util.Optional;

@Service
public class OrderOperationService {
    private transient OrderService orderService;
    private transient StoreService storeService;
    private transient MailingService mailingService;

    @Autowired
    public OrderOperationService(OrderService orderService, StoreService storeService, MailingService mailingService) {
        this.orderService = orderService;
        this.storeService = storeService;
        this.mailingService = mailingService;
    }

    public ResponseEntity placeOrder(Order incoming, String userId) throws Exception {
        //check if the order that is trying to be placed is by the user the request comes from
        //if not then we deny the operation, else we process the order (and validate everything else)

        if (!userId.equals(incoming.getUserId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header(HttpHeaders.WARNING, "You are trying to place an order for someone else").build();
        }

        //return the order we just processed to the user
        Order processed = orderService.processOrder(incoming);

        Long storeId = processed.getStoreId();
        String recipientEmail = storeService.getEmailById(storeId);

        sendEmail(processed.getOrderId(), recipientEmail, MailingService.ProcessType.CREATED);

        return ResponseEntity.status(HttpStatus.CREATED).body(processed);
    }

    public ResponseEntity editOrder(Order incoming, String userId) throws Exception {
        if (!userId.equals(incoming.getUserId())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).header(HttpHeaders.WARNING, "You are trying to edit an order from someone else").build();
        }

        //return the order we just processed to the user
        Order processed = orderService.processOrder(incoming);

        Long storeId = processed.getStoreId();
        String recipientEmail = storeService.getEmailById(storeId);

        sendEmail(processed.getOrderId(), recipientEmail, MailingService.ProcessType.EDITED);

        return ResponseEntity.status(HttpStatus.CREATED).body(processed);
    }

    @SuppressWarnings("PMD")
    public ResponseEntity deleteOrder(DeleteModel deleteModel, String userId, String roleString) {
        //check if the user is a manager
        boolean isManager = roleString.equals("[ROLE_MANAGER]");

        Optional<Order> orderToBeDeleted = orderService.findOrder(deleteModel.getOrderId());

        if (orderToBeDeleted.isPresent()) {
            Long storeId = orderToBeDeleted.get().getStoreId();
            String recipientEmail = storeService.getEmailById(storeId);

            if (!orderService.removeOrder(deleteModel.getOrderId(), userId, isManager)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            sendEmail(deleteModel.getOrderId(), recipientEmail, MailingService.ProcessType.DELETED);
            //validate if we can delete this order, if we can ok else bad request
            return ResponseEntity.status(HttpStatus.OK).build();
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    public void sendEmail(long id, String recipientEmail, MailingService.ProcessType processType) {
        mailingService.sendEmail(id, recipientEmail, processType);
    }
}
