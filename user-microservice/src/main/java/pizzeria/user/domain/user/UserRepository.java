package pizzeria.user.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * A DDD repository for quering and persisting user aggregate roots.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {
    /**
     * Return all the users
     * @return
     */
    List<User> findAll();

    /**
     * Delete a user, given his id
     * @param id ID of the user
     * @return Number indicating how many users have been deleted
     */
    @Transactional
    Long deleteUserByEmail(String id);

    /**
     * Indicates whether a user with the given email already exists in our database
     * @param email user's email
     * @return True or False, indicating whether it already exists
     */
    boolean existsByEmail(String email);

    //Optional<User> findUserByAllergies();

    /**
     * Finds a user, given his id
     * @param id ID of the user
     * @return Optional that contains the user in case he exists in the database
     */
    Optional<User> findUserById(String id);

    /**
     * Finds a user, given his email(emails are unique)
     * @param email Email of the user
     * @return Optional that contains the user in case he exists in the database
     */
    Optional<User> findUserByEmail(String email);
}
