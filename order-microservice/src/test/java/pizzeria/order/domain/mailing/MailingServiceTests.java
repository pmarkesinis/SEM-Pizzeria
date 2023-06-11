package pizzeria.order.domain.mailing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test", "mockMessageTransport"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class MailingServiceTests {
    @Autowired
    private MessageTransport messageTransport;

    @Autowired
    private MailingService mailingService;

    @ParameterizedTest
    @MethodSource("sendMessageSuite")
    public void sendMessage(MailingService.ProcessType type) throws Exception{
        doNothing().when(messageTransport).sendMessage(any());

        mailingService.sendEmail(1L, "tomsfighter@gmail.com", type);

        verify(messageTransport, times(1)).sendMessage(any());
    }

    static Stream<Arguments> sendMessageSuite() {
        return Stream.of(
          Arguments.of(MailingService.ProcessType.CREATED),
          Arguments.of(MailingService.ProcessType.DELETED),
          Arguments.of(MailingService.ProcessType.EDITED)
        );
    }

    @Test
    public void throwsError() throws Exception{
        doThrow(new MessagingException()).when(messageTransport).sendMessage(any());

        mailingService.sendEmail(1L, "tomsfighter@gmail.com", MailingService.ProcessType.CREATED);

        verify(messageTransport, times(1)).sendMessage(any());
        assertThat(mailingService.getLogg()).containsExactlyElementsOf(List.of("Couldn't send email to tomsfighter@gmail.com"));
    }

    @ParameterizedTest
    @MethodSource("typesSuite")
    public void createdOrder(MailingService.ProcessType type,
                             String typeMessage,
                             String bodyText) throws MessagingException, IOException {
        ArgumentCaptor<MimeMessage> messageArgumentCaptor = ArgumentCaptor.forClass(MimeMessage.class);

        mailingService.sendEmail(1L, "mockedEmail", type);
        verify(messageTransport, times(1)).sendMessage(messageArgumentCaptor.capture());

        MimeMessage message = messageArgumentCaptor.getValue();

        assertThat(message.getFrom()[0]).isEqualTo(new InternetAddress("fivenightsatandys7b@gmail.com"));
        assertThat(message.getAllRecipients()[0]).isEqualTo(new InternetAddress("mockedEmail"));
        assertThat(message.getSubject()).isEqualTo(typeMessage);
        assertThat(message.getContent().toString()).isEqualTo(bodyText);
    }

    static Stream<Arguments> typesSuite() {
        return Stream.of(
                Arguments.of(MailingService.ProcessType.CREATED, "Order has been created!", "Order with orderId : 1 has been created"),
                Arguments.of(MailingService.ProcessType.DELETED, "Order has been deleted!", "Order with orderId : 1 has been deleted"),
                Arguments.of(MailingService.ProcessType.EDITED, "Order has been edited!", "Order with orderId : 1 has been edited")
        );
    }
}
