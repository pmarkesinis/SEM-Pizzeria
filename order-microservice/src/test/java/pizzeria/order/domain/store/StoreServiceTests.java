package pizzeria.order.domain.store;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
@SpringBootTest
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class StoreServiceTests {
    @Autowired
    private transient StoreService storeService;

    @Autowired
    private transient StoreRepository storeRepository;

    @Test
    void addStore_worksCorrectly() throws Exception {
        Store newstore = new Store("NL-2624ME", "borislavsemerdzhiev.02@gmail.com");
        storeService.addStore(newstore);

        Optional<Store> actualStore = storeRepository.findById(newstore.getId());

        assertThat(actualStore.isPresent()).isTrue();

        assertThat(actualStore.get().getLocation()).isEqualTo(newstore.getLocation());
        assertThat(actualStore.get().getContact()).isEqualTo(newstore.getContact());
    }

    @ParameterizedTest
    @MethodSource("invalidArgumentsSuite")
    void addStore_invalidArguments(Store store, Class exception) throws Exception{
        assertThatThrownBy(() -> {
            storeService.addStore(store);
        }).isInstanceOf(exception);

        assertThat(storeRepository.existsById(1L)).isFalse();
    }

    @Test
    void addStore_storeIsNull() throws Exception{
        StoreService.StoreIsNullException exception = assertThrows(StoreService.StoreIsNullException.class, () -> {
            storeService.addStore(null);
        });

        assertThat(exception.getMessage()).isEqualTo("The store that is provided is null");
    }

    @Test
    void addStore_storeAlreadyExists() throws Exception{
        Store store = new Store("NL-2624ME", "bor@gmail.com");
        storeService.addStore(store);
        StoreService.StoreAlreadyExistException exception = assertThrows(StoreService.StoreAlreadyExistException.class, () -> {
            storeService.addStore(store);
        });

        assertThat(exception.getMessage()).isEqualTo("There already exists a store with the same id");
    }

    static Stream<Arguments> invalidArgumentsSuite() {
        final String correctEmail = "borislavsemerdzhiev.02@gmail.com";
        final String correctLocation = "NL-2624ME";
        return Stream.of(
                Arguments.of(null, StoreService.StoreIsNullException.class),
                Arguments.of(new Store("Nl-2624ME", correctEmail), StoreService.InvalidLocationException.class),
                Arguments.of(new Store(correctLocation, "borislav@gmail."), StoreService.InvalidEmailException.class),
                Arguments.of(new Store(correctLocation, "borislav@gmail"), StoreService.InvalidEmailException.class),
                Arguments.of(new Store(correctLocation, "@gmail.com"), StoreService.InvalidEmailException.class),
                Arguments.of(new Store(correctLocation, "bbb@gmail."), StoreService.InvalidEmailException.class),
                Arguments.of(new Store(correctLocation, "borislav@gmail."), StoreService.InvalidEmailException.class),
                Arguments.of(new Store(correctLocation, "borislav@a.com"), StoreService.InvalidEmailException.class),
                Arguments.of(new Store(correctLocation, "borislava.com"), StoreService.InvalidEmailException.class),
                Arguments.of(new Store("NL-262ME", correctEmail), StoreService.InvalidLocationException.class),
                Arguments.of(new Store("Nl-2624ME", correctEmail), StoreService.InvalidLocationException.class),
                Arguments.of(new Store("NL-2624Me", correctEmail), StoreService.InvalidLocationException.class),
                Arguments.of(new Store("NL-ME", correctEmail), StoreService.InvalidLocationException.class),
                Arguments.of(new Store("NL-99991ME", correctEmail), StoreService.InvalidLocationException.class),
                Arguments.of(new Store("NL999ME", correctEmail), StoreService.InvalidLocationException.class),
                Arguments.of(new Store("NL999M2", correctEmail), StoreService.InvalidLocationException.class),
                Arguments.of(new Store("NL99932", correctEmail), StoreService.InvalidLocationException.class)
        );
    }

    @Test
    void editStore_worksCorrectly() throws Exception {
        Store newstore = new Store("NL-2624ME", "borislavsemerdzhiev.02@gmail.com");
        storeService.addStore(newstore);

        Store editedStore = new Store("NL-2020ME", "newborislav.02@gmail.com");

        storeService.editStore(1L, editedStore);

        Optional<Store> actualStore = storeRepository.findById(1L);

        assertThat(actualStore.isPresent()).isTrue();
        assertThat(actualStore.get().getContact()).isEqualTo(editedStore.getContact());
        assertThat(actualStore.get().getLocation()).isEqualTo(editedStore.getLocation());
    }

    @Test
    void editStore_noSuchId() throws Exception {
        Store newstore = new Store("NL-2624ME", "borislavsemerdzhiev.02@gmail.com");
        storeService.addStore(newstore);

        Store editedStore = new Store("NL-2020ME", "newborislav.02@gmail.com");

        assertThatThrownBy(() -> {
            storeService.editStore(2L, editedStore);
        }).isInstanceOf(StoreService.StoreDoesNotExistException.class);

        Optional<Store> actualStore = storeRepository.findById(1L);

        assertThat(actualStore.isPresent()).isTrue();
        assertThat(actualStore.get().getContact()).isEqualTo(newstore.getContact());
        assertThat(actualStore.get().getLocation()).isEqualTo(newstore.getLocation());
    }

    @ParameterizedTest
    @MethodSource("editInvalidArgumentsSuite")
    void editStore_invalidArguments(Store editStore, Class exception) throws Exception {
        Store newstore = new Store("NL-2624ME", "borislavsemerdzhiev.02@gmail.com");
        storeService.addStore(newstore);

        assertThatThrownBy(() -> {
            storeService.editStore(1L, editStore);
        }).isInstanceOf(exception);

        Optional<Store> actualStore = storeRepository.findById(1L);

        assertThat(actualStore.isPresent()).isTrue();
        assertThat(actualStore.get().getContact()).isEqualTo(newstore.getContact());
        assertThat(actualStore.get().getLocation()).isEqualTo(newstore.getLocation());
    }

    static Stream<Arguments> editInvalidArgumentsSuite() {
        final String correctEmail = "borislavsemerdzhiev.02@gmail.com";
        final String correctLocation = "NL-2624ME";
        return Stream.of(
                Arguments.of(new Store("Nl-2624ME", correctEmail), StoreService.InvalidLocationException.class),
                Arguments.of(new Store(correctLocation, "borislav@gmail."), StoreService.InvalidEmailException.class),
                Arguments.of(new Store(correctLocation, "borislav@gmail"), StoreService.InvalidEmailException.class),
                Arguments.of(new Store(correctLocation, "@gmail.com"), StoreService.InvalidEmailException.class),
                Arguments.of(new Store(correctLocation, "bbb@gmail."), StoreService.InvalidEmailException.class),
                Arguments.of(new Store(correctLocation, "borislav@gmail."), StoreService.InvalidEmailException.class),
                Arguments.of(new Store(correctLocation, "borislav@a.com"), StoreService.InvalidEmailException.class),
                Arguments.of(new Store(correctLocation, "borislava.com"), StoreService.InvalidEmailException.class),
                Arguments.of(new Store("NL-262ME", correctEmail), StoreService.InvalidLocationException.class),
                Arguments.of(new Store("Nl-2624ME", correctEmail), StoreService.InvalidLocationException.class),
                Arguments.of(new Store("NL-2624Me", correctEmail), StoreService.InvalidLocationException.class),
                Arguments.of(new Store("NL-ME", correctEmail), StoreService.InvalidLocationException.class),
                Arguments.of(new Store("NL-99991ME", correctEmail), StoreService.InvalidLocationException.class),
                Arguments.of(new Store("NL999ME", correctEmail), StoreService.InvalidLocationException.class),
                Arguments.of(new Store("NL999M2", correctEmail), StoreService.InvalidLocationException.class),
                Arguments.of(new Store("NL99932", correctEmail), StoreService.InvalidLocationException.class)
        );
    }

    @Test
    void deleteStore_deletesCorrectly() throws Exception {
        Store newstore = new Store("NL-2624ME", "borislavsemerdzhiev.02@gmail.com");
        storeService.addStore(newstore);

        storeService.getStoreRepo().deleteStoreById(1L);

        assertThat(storeRepository.existsById(1L)).isFalse();
    }

    @Test
    void deleteStore_noSuchStore() throws Exception {
        Store newstore = new Store("NL-2624ME", "borislavsemerdzhiev.02@gmail.com");
        storeService.addStore(newstore);

        storeService.getStoreRepo().deleteStoreById(2L);
        assertThat(storeRepository.existsById(1L)).isTrue();
    }

    @Test
    void deleteStore_alreadyDeleted() throws Exception {
        Store newstore = new Store("NL-2624ME", "borislavsemerdzhiev.02@gmail.com");
        storeService.addStore(newstore);

        storeService.getStoreRepo().deleteStoreById(1L);

        storeService.getStoreRepo().deleteStoreById(2L);
        assertThat(storeRepository.existsById(1L)).isFalse();
    }

    @Test
    void getEmailById_worksCorrectly() throws Exception {
        Store newstore = new Store("NL-2624ME", "borislavsemerdzhiev.02@gmail.com");
        storeService.addStore(newstore);

        String actualEmail = storeService.getEmailById(1L);

        assertThat(actualEmail).isEqualTo(newstore.getContact());
    }

    @Test
    void getEmailById_noSuchId() throws Exception {
        Store newstore = new Store("NL-2624ME", "borislavsemerdzhiev.02@gmail.com");
        storeService.addStore(newstore);

        String actualEmail = storeService.getEmailById(2L);

        assertThat(actualEmail).isEqualTo(null);
    }

    @Test
    void existsById_worksCorrectly() throws Exception {
        Store newstore = new Store("NL-2624ME", "borislavsemerdzhiev.02@gmail.com");
        storeService.addStore(newstore);

        assertThat(storeService.getStoreRepo().existsById(1L)).isTrue();
    }

    @Test
    void existsById_noSuchId() throws Exception {
        Store newstore = new Store("NL-2624ME", "borislavsemerdzhiev.02@gmail.com");
        storeService.addStore(newstore);

        assertThat(storeService.getStoreRepo().existsById(2L)).isFalse();
    }

    @Test
    void getAllStores() throws Exception {
        Store newStore = new Store("NL-2624ME", "borislavsemerdzhiev.02@gmail.com");
        Store newStore2 = new Store("NL-2625ME", "borislavsemerdzhiev2.02@gmail.com");
        Store newStore3 = new Store("NL-2626ME", "borislavsemerdzhiev3.02@gmail.com");
        storeService.addStore(newStore);
        storeService.addStore(newStore2);
        storeService.addStore(newStore3);

        assertThat(storeService.getStoreRepo().findAll()).containsExactlyInAnyOrderElementsOf(List.of(newStore, newStore2, newStore3));
    }

    @Test
    void testAddStore() throws Exception {
        Store newStore = new Store("NL-2624ME", "email@gmail.com");
        //verify that the method add store returns the newstore and that the store is added to the repository
        assertThat(storeService.addStore(newStore)).isEqualTo(newStore);
        assertThat(storeService.getStoreRepo().findAll()).containsExactlyInAnyOrderElementsOf(List.of(newStore));
    }

    @Test
    void testDeleteStore() throws StoreService.StoreDoesNotExistException {
        Store newStore = new Store("NL-2624ME", "email@gmail.com");
        storeRepository.save(newStore);
        storeService.deleteStore(newStore.getId());
        assertThat(storeRepository.findAll()).isEmpty();
    }

    @Test
    void testDeleteStore2(){
        try {
            storeService.deleteStore(1L);
        } catch (StoreService.StoreDoesNotExistException e) {
            assertThat(e.getMessage()).isEqualTo("The store with the id provided does not exist");
        }
    }

    @Test
    void testInvalidLocation() {
        Store newStore = new Store("NL-26ME", "email@gmail.com");
        try {
            storeService.addStore(newStore);
        } catch (StoreService.InvalidLocationException e) {
            assertThat(e.getMessage()).isEqualTo("Invalid location format");
        } catch (Exception e) {
            fail();
        }
    }

    @Test
    void testInvalidEmailFormat(){
        Store newStore = new Store("NL-2624ME", "emailgmail.com");
        try {
            storeService.addStore(newStore);
        } catch (StoreService.InvalidEmailException e) {
            assertThat(e.getMessage()).isEqualTo("Invalid email format");
        } catch (Exception e) {
            fail();
        }
    }
}
