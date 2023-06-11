package pizzeria.order.domain.store;

import com.sun.istack.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name="stores")
@NoArgsConstructor
public class Store {
    @Id
    @Column(name = "id")
    @NotNull
    @Getter
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter
    @EqualsAndHashCode.Include
    private long id;

    @Column(name = "location")
    @Getter
    @Setter
    @EqualsAndHashCode.Exclude
    private String location;

    @Column(name = "contact")
    @Getter
    @Setter
    @EqualsAndHashCode.Exclude
    private String contact;

    public Store(String location, String contact) {
        this.location = location;
        this.contact = contact;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Store)) return false;
        Store store = (Store) o;
        return id == store.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
