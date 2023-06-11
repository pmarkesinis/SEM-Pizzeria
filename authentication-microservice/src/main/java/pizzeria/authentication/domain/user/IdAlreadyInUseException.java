package pizzeria.authentication.domain.user;

/**
 * Exception to indicate the NetID is already in use.
 */
public class IdAlreadyInUseException extends Exception {
    static final long serialVersionUID = -3387516993124229948L;
    
    public IdAlreadyInUseException(String netId) {
        super(netId);
    }
}
