package pizzeria.user.models;

import lombok.Data;
import pizzeria.user.domain.user.User;
import java.util.List;

@Data
public class UserRegisterModel {
    String email;
    List<String> allergies;
    String name;
    String password;
    public User parseToUser() {
        return new User(name, email, allergies);
    }
}
