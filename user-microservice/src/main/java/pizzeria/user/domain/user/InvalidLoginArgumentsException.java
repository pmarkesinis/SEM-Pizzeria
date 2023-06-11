package pizzeria.user.domain.user;

import pizzeria.user.models.LoginModel;

/**
 * Exception to indicate the NetID is already in use.
 */
public class InvalidLoginArgumentsException extends Exception {
    static final long serialVersionUID = -4387516993124229949L;

    public InvalidLoginArgumentsException(LoginModel user) {
        super(user.toString());
    }
}
