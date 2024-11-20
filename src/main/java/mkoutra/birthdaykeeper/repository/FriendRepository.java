package mkoutra.birthdaykeeper.repository;

import mkoutra.birthdaykeeper.model.Friend;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    List<Friend> findFriendsByLastname(String lastname);
//    List<Friend> findFriendsByDateOfBirth_Month(Short month);
    Optional<Friend> findFriendByUuid(String uuid);
    Optional<Friend> findFriendByFirstnameAndLastnameAndUserId(String firstname, String lastname, Long id);
}
