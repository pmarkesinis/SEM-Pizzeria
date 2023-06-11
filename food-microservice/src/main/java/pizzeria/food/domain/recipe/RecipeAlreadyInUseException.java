package pizzeria.food.domain.recipe;

public class RecipeAlreadyInUseException extends Exception{
    static final long serialVersionUID = -3437516993124229948L;
    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public RecipeAlreadyInUseException() {
        super("The recipe is already stored in the database");
    }

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public RecipeAlreadyInUseException(String message) {
        super(message);
    }

    /**
     * @return a String value that is associated with this RecipeAlreadyInUseException
     */
    @Override
    public String getMessage() {
        return super.getMessage();
    }

}
