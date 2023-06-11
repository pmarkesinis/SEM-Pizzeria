package pizzeria.user.domain.user;

/**
 * Exception to indicate the NetID is already in use.
 */
public class EmailAlreadyInUseException extends Exception {
    static final long serialVersionUID = -3387516993124229949L;

    public EmailAlreadyInUseException(String email) {
        super(email);
    }
}
