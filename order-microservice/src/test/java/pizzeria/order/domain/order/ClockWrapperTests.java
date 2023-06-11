package pizzeria.order.domain.order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ClockWrapperTests {
    @Autowired
    private transient ClockWrapper clockWrapper;

    @Test
    void timeTest() {
        // might be a bit flaky if the method takes too time to run
        assertThat(clockWrapper.getNow().plusMinutes(5).isAfter(LocalDateTime.now())).isTrue();
        assertThat(clockWrapper.getNow().minusMinutes(5).isBefore(LocalDateTime.now())).isTrue();
    }
}
