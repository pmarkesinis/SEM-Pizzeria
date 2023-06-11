package pizzeria.authentication.domain.user;

/**
 * A DDD domain event that indicated a user was created.
 */
public class UserWasCreatedEvent {
    private final String id;

    public UserWasCreatedEvent(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}
