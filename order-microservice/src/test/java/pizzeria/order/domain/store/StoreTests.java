package pizzeria.order.domain.store;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest
// activate profiles to have spring use mocks during auto-injection of certain beans.
@ActiveProfiles({"test"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class StoreTests {

    @Test
    public void testStore_constructor() {
        Store store = new Store("NL-2624ME", "borislav@gmail.com");
        Object otherObject = "Random String";

        assertThat(store.equals(otherObject)).isFalse();
    }

    @Test
    public void testStore_sameId() {
        Store store = new Store("NL-2624ME", "borislav@gmail.com");
        store.setId(1);
        Store store2 = new Store("NL-2624ME", "borislav@gmail.com");
        store2.setId(1);

        assertThat(store.equals(store2)).isTrue();
    }

    @Test
    public void testStore_differentIds() {
        Store store = new Store("NL-2624ME", "borislav@gmail.com");
        store.setId(1);
        Store store2 = new Store("NL-2624ME", "borislav@gmail.com");
        store2.setId(2);

        assertThat(store.equals(store2)).isFalse();
    }

    @Test
    public void testStore_differentObjects() {
        Store store = new Store("NL-2624ME", "borislav@gmail.com");
        Store store2 = new Store("NL-2624ME", "borislav@gmail.com");
        store2.setId(store.getId());

        assertThat(store.equals(store2)).isTrue();
    }
}
