package pizzeria.user.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pizzeria.user.domain.user.InvalidLoginArgumentsException;
import pizzeria.user.models.LoginModel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class InvalidLoginArgumentsExceptionTest {

    private LoginModel model;

    @BeforeEach
    void init(){
        String email = "test";
        String password = "testpwd";

        model = new LoginModel();
        model.setEmail(email);
        model.setPassword(password);
    }

    @Test
    void testConstructor(){
        InvalidLoginArgumentsException exception = new InvalidLoginArgumentsException(model);
        assertThat(exception.getMessage()).isEqualTo(model.toString());
        assertThat(exception).isNotNull();
    }


    @Test
    void getMessage() {
        InvalidLoginArgumentsException exception = new InvalidLoginArgumentsException(model);
        assertThat(exception.getMessage()).isEqualTo(model.toString());
    }

    @Test
    void testThrows(){
        assertThrows(InvalidLoginArgumentsException.class, () -> {
            throw new InvalidLoginArgumentsException(model);
        });
    }
}