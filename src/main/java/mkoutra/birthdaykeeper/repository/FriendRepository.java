package mkoutra.birthdaykeeper.repository;

import mkoutra.birthdaykeeper.model.Friend;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRepository extends JpaRepository<Friend, Long> {
    Optional<Friend> findFriendByFirstnameAndLastnameAndUserId(String firstname, String lastname, Long id);
    List<Friend> findFriendsByUserId(Long id);

    @Query("SELECT COUNT(f) > 0 " +
            "FROM Friend f JOIN f.user u " +
            "WHERE f.id = :friendId AND u.username = :username")
    boolean existsByFriendIdAndUsername(@Param("friendId") Long friendId, @Param("username") String username);

    Page<Friend> findFriendsByUserId(Long userId, Pageable pageable);
}
