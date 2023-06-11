package pizzeria.order.domain.order;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * The type Clock wrapper.
 * Wraps local date time inside a class for mocking purposes
 */
@Component
public class ClockWrapper {
    /**
     * Get the current local date time.
     *
     * @return the local date time
     */
    public LocalDateTime getNow(){
        return LocalDateTime.now();
    }
}
