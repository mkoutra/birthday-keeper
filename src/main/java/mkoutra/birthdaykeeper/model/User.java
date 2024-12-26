package mkoutra.birthdaykeeper.model;

import jakarta.persistence.*;
import lombok.*;
import mkoutra.birthdaykeeper.core.enums.Role;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users",
        indexes = @Index(name = "Idx_User_username", columnList = "username", unique = true))   // not sure if it is necessary. An index for username was automatically created by the db, probably due to the unique=true @Column field
public class User extends AbstractEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, updatable = false, length = 40)
    private String username;

    @Column(nullable = false, length = 60)  // length 60 for Blowfish cipher
    private String password;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Getter(value = AccessLevel.PRIVATE)
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Friend> friends = new HashSet<>();

    public Set<Friend> getAllFriends() {
        return Collections.unmodifiableSet(friends);
    }

    public void addFriend(Friend friend) {
        if (friend != null) {
            friends.add(friend);
            friend.setUser(this);
        }
    }

    public void removeFriend(Friend friend) {
        if (friend != null) {
            friend.setUser(null);
            friends.remove(friend);
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // TODO: Add Soft Deletion and modify isEnabled() accordingly
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", role=" + role +
                '}';
    }
}
