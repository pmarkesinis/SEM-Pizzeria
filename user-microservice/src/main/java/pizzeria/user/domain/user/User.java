package pizzeria.user.domain.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {
    @Id
    @Column(name = "id", nullable = false)
    //@GeneratedValue(strategy = GenerationType.AUTO)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Getter
    @Setter
    private String id;

    @Column(name = "name", nullable = false)
    @Getter
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    @Getter
    private String email;
    @Column(name = "allergies", nullable = false)
    @ElementCollection(fetch = FetchType.EAGER)
    @Getter
    @Setter
    private List<String> allergies;

    public User(String name, String email, List<String> allergies) {
        this.name = name;
        this.email = email;
        this.allergies = allergies;
        //this.recordThat(new UserWasCreatedEvent(netId));
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
