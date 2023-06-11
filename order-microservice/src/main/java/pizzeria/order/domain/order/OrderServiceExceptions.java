package pizzeria.order.domain.order;

import java.util.Objects;

public class OrderServiceExceptions {
    /**
     * The type Price not right exception.
     */
    @SuppressWarnings("PMD")
    public static class PriceNotRightException extends Exception {
        private final String msg;
        public PriceNotRightException(String msg) {
            this.msg = Objects.requireNonNullElse(msg, "[err loading price]");
        }
        @Override
        public String getMessage(){
            return "The price calculated does not match the price given: " + msg;
        }
    }

    /**
     * Store id does not exist
     */
    @SuppressWarnings("PMD")
    public static class InvalidStoreIdException extends Exception {
        @Override
        public String getMessage(){
            return "The store id does not exist";
        }
    }

    /**
     * The type Time invalid exception.
     */
    @SuppressWarnings("PMD")
    public static class TimeInvalidException extends Exception {
        @Override
        public String getMessage(){
            return "The selected pickup time is not valid.";
        }
    }

    /**
     * The type Could not store exception.
     */
    @SuppressWarnings("PMD")
    public static class CouldNotStoreException extends Exception {
        @Override
        public String getMessage(){
            return "The order is null or it already exists in the database.";
        }
    }

    /**
     * The type Food invalid exception.
     */
    @SuppressWarnings("PMD")
    public static class FoodInvalidException extends Exception {
        @Override
        public String getMessage(){
            return "The order contains invalid recipe/ingredient ids.";
        }
    }

    /**
     * The type Invalid edit exception.
     */
    @SuppressWarnings("PMD")
    public static class InvalidEditException extends Exception {
        @Override
        public String getMessage(){
            return "The order does not belong to the same user.";
        }
    }
}
