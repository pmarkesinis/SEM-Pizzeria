package pizzeria.order.domain.mailing;

import org.springframework.stereotype.Component;
import pizzeria.order.Application;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

@Component
public class MessageTransport {
    @Application.ExcludeFromJacocoGeneratedReport
    public void sendMessage(MimeMessage message) throws MessagingException {
        Transport.send(message);
    }
}
