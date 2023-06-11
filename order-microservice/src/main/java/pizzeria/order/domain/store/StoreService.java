package pizzeria.order.domain.store;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class StoreService {
    @Getter
    private final transient StoreRepository storeRepo;

    @Autowired
    public StoreService(StoreRepository storeRepo){
        this.storeRepo = storeRepo;
    }

    public Store addStore(Store store) throws Exception {
        if (store == null) {
            throw new StoreIsNullException();
        }
        if (storeRepo.existsById(store.getId()))
            throw new StoreAlreadyExistException();
        verifyEmailFormat(store.getContact());
        verifyLocationFormat(store.getLocation());
        return storeRepo.save(store);
    }

    public void editStore(Long id, Store store) throws Exception {
        Optional<Store> optionalStore = storeRepo.findById(id);
        if (optionalStore.isEmpty())
            throw new StoreDoesNotExistException();
        verifyEmailFormat(store.getContact());
        verifyLocationFormat(store.getLocation());
        optionalStore.get().setContact(store.getContact());
        optionalStore.get().setLocation(store.getLocation());
        storeRepo.save(optionalStore.get());
    }

    public void deleteStore(Long id) throws StoreDoesNotExistException {
        if (!storeRepo.existsById(id)) {
            throw new StoreDoesNotExistException();
        }
        storeRepo.deleteStoreById(id);
    }

    /**
     * Get the email corresponding to the storeID
     * @param id ID of the store
     * @return Email of the corresponding store
     */
    public String getEmailById(Long id) {
        if (!storeRepo.existsById(id)) {
            return null;
        }
        return storeRepo.findById(id).orElse(new Store(null, null)).getContact();
    }

    private void verifyEmailFormat(String testEmail) throws InvalidEmailException {
        String regexPattern = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"
                + "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(regexPattern);
        if (!pattern.matcher(testEmail).matches())
            throw new InvalidEmailException();
    }

    private void verifyLocationFormat(String testLocation) throws InvalidLocationException {
        String regexPattern = "^(?:NL-)?(\\d{4})\\s*([A-Z]{2})$";

        Pattern pattern = Pattern.compile(regexPattern);
        if (!pattern.matcher(testLocation).matches())
            throw new InvalidLocationException();
    }

    @SuppressWarnings("PMD")
    public static class StoreDoesNotExistException extends Exception {
        @Override
        public String getMessage(){
            return "The store with the id provided does not exist";
        }
    }

    @SuppressWarnings("PMD")
    public static class StoreIsNullException extends Exception {
        @Override
        public String getMessage(){
            return "The store that is provided is null";
        }
    }

    @SuppressWarnings("PMD")
    public static class StoreAlreadyExistException extends Exception {
        @Override
        public String getMessage(){
            return "There already exists a store with the same id";
        }
    }

    @SuppressWarnings("PMD")
    public static class InvalidEmailException extends Exception {
        @Override
        public String getMessage(){
            return "Invalid email format";
        }
    }

    @SuppressWarnings("PMD")
    public static class InvalidLocationException extends Exception {
        @Override
        public String getMessage(){
            return "Invalid location format";
        }
    }
}
