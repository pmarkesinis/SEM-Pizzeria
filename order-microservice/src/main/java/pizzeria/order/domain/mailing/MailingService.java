package pizzeria.order.domain.mailing;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pizzeria.order.Application;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Service
public class MailingService {
    // The email we use to send messages from
    final transient String fromEmail = "fivenightsatandys7b@gmail.com";
    // The authentication password we use
    final transient String fromPassword = "ycgmcwdcuopnrbrd";

    final transient MessageTransport messageTransport;

    public enum ProcessType {
        CREATED,
        EDITED,
        DELETED
    }

    @Getter
    transient private Session session;

    @Getter
    transient private List<String> logg;
    private final transient String[] messageSubject = {
            "Order has been created!",
            "Order has been edited!",
            "Order has been deleted!"
    };
    @Autowired
    public MailingService(MessageTransport messageTransport) {
        this.messageTransport = messageTransport;
        logg = new ArrayList<>();

        String host = "smtp.gmail.com";

        // Get system properties
        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        // Get the Session object.// and pass username and password
        session = Session.getInstance(properties, new javax.mail.Authenticator() {
            @Application.ExcludeFromJacocoGeneratedReport
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, fromPassword);
            }
        });

        // Used to debug SMTP issues
        //session.setDebug(true);
    }

    private String getMessageText(Long orderId, int index) {
        String[] messageText = {
                String.format("Order with orderId : %d has been created", orderId),
                String.format("Order with orderId : %d has been edited", orderId),
                String.format("Order with orderId : %d has been deleted", orderId)
        };
        return messageText[index];
    }

    /**
     * Notify the store about the creation/edit/deletion of an order with the current orderId
     * @param orderId ID of the order
     * @param recipientEmail Email of the store
     * @param processType Type of the process CREATED/EDITED/DELETED
     */
    @SuppressWarnings("PMD")
    public void sendEmail(Long orderId, String recipientEmail, ProcessType processType) {
        try {
            // Create a default MimeMessage object.
            MimeMessage message = new MimeMessage(session);

            // Set From: header field of the header.
            message.setFrom(new InternetAddress(fromEmail));

            // Set To: header field of the header.
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));

            int messageTypeToNumber = processType.ordinal();
            //System.out.println(messageTypeToNumber + " " + messageSubject[messageTypeToNumber]);

            // Set Subject: header field
            message.setSubject(messageSubject[messageTypeToNumber]);
            // Now set the actual message
            message.setText(getMessageText(orderId, messageTypeToNumber));
            // Send message
            messageTransport.sendMessage(message);
        } catch (MessagingException mex) {
            logg.add("Couldn't send email to " + recipientEmail);
        }
    }

}
