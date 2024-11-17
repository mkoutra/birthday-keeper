package mkoutra.birthdaykeeper.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "users")
public class User extends AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user")
    @Getter(value = AccessLevel.PRIVATE)
    private Set<Friend> friends = new HashSet<>();

    public Set<Friend> getAllFriends() {
        return Collections.unmodifiableSet(friends);
    }

    public void addFriend(Friend friend) {
        if (friends == null) {
            friends = new HashSet<>();
        }
        friends.add(friend);
        friend.setUser(this);
    }

    public void removeFriend(Friend friend) {
        if (friend == null) {
            return;
        }
        friends.remove(friend);
        friend.setUser(null);
    }
}
