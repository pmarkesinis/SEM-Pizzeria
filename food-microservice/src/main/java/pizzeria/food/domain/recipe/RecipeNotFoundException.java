package pizzeria.food.domain.recipe;

public class RecipeNotFoundException extends Exception{
    static final long serialVersionUID = -3387416993124229948L;

    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public RecipeNotFoundException() {
        super("The recipe could not be found in the database");
    }

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public RecipeNotFoundException(String message) {
        super(message);
    }

    /**
     * @return String that represents the message associated with this RecipeNotFoundException
     */
    @Override
    public String getMessage() {
        return super.getMessage();
    }

}
