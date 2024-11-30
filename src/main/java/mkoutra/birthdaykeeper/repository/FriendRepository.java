package mkoutra.birthdaykeeper.repository;

import mkoutra.birthdaykeeper.model.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    Optional<Friend> findFriendByFirstnameAndLastnameAndUserId(String firstname, String lastname, Long id);
    List<Friend> findFriendsByUserId(Long id);
}
