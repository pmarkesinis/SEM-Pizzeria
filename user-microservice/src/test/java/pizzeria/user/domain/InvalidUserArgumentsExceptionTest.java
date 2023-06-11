package pizzeria.user.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pizzeria.user.domain.user.InvalidUserArgumentsException;
import pizzeria.user.models.UserRegisterModel;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InvalidUserArgumentsExceptionTest {

    private UserRegisterModel model;

    @BeforeEach
    void init(){
        String email = "test";
        String password = "testpwd";
        String name = "testName";
        List<String> allergies = List.of("Al1", "Al2");

        model = new UserRegisterModel();
        model.setName(name);
        model.setEmail(email);
        model.setPassword(password);
        model.setAllergies(allergies);
    }

    @Test
    void testConstructor(){
        InvalidUserArgumentsException exception = new InvalidUserArgumentsException(model);
        assertThat(exception.getMessage()).isEqualTo(model.toString());
        assertThat(exception).isNotNull();
    }


    @Test
    void getMessage() {
        InvalidUserArgumentsException exception = new InvalidUserArgumentsException(model);
        assertThat(exception.getMessage()).isEqualTo(model.toString());
    }

    @Test
    void testThrows(){
        assertThrows(InvalidUserArgumentsException.class, () -> {
            throw new InvalidUserArgumentsException(model);
        });
    }
}