package mkoutra.birthdaykeeper.mapper;

import lombok.RequiredArgsConstructor;
import mkoutra.birthdaykeeper.core.enums.Role;
import mkoutra.birthdaykeeper.dto.friendDTOs.FriendInsertDTO;
import mkoutra.birthdaykeeper.dto.friendDTOs.FriendReadOnlyDTO;
import mkoutra.birthdaykeeper.dto.friendDTOs.FriendUpdateDTO;
import mkoutra.birthdaykeeper.dto.userDTOs.UserInsertDTO;
import mkoutra.birthdaykeeper.dto.userDTOs.UserReadOnlyDTO;
import mkoutra.birthdaykeeper.model.Friend;
import mkoutra.birthdaykeeper.model.User;
import mkoutra.birthdaykeeper.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Mapper {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Friend mapToFriend(FriendInsertDTO dto) {
        Friend friend = new Friend();
        friend.setFirstname(dto.getFirstname());
        friend.setLastname(dto.getLastname());
        friend.setNickname(dto.getNickname());
        friend.setDateOfBirth(dto.getDateOfBirth());
        return friend;
    }

    public Friend mapToFriend(FriendUpdateDTO dto) {
        return new Friend(
                Long.parseLong(dto.getId()),
                dto.getFirstname(),
                dto.getLastname(),
                dto.getNickname(),
                dto.getDateOfBirth(),
                null
        );
    }

    public FriendReadOnlyDTO mapToFriendReadOnlyDTO(Friend friend) {
        return new FriendReadOnlyDTO(
                friend.getId().toString(),
                friend.getFirstname(),
                friend.getLastname(),
                friend.getNickname(),
                friend.getDateOfBirth()
        );
    }

    public User mapToUser(UserReadOnlyDTO userReadOnlyDTO) {
        return userRepository.findUserByUsername(userReadOnlyDTO.getUsername()).orElse(null);
    }

    public User mapToUser(UserInsertDTO userInsertDTO) {
        User user = new User();
        user.setUsername(userInsertDTO.getUsername());
        user.setPassword(passwordEncoder.encode(userInsertDTO.getPassword()));  // Hash password with BCrypt
        user.setRole(userInsertDTO.getRole());
        return user;
    }

    public UserReadOnlyDTO mapToUserReadOnlyDTO(User user) {
        return new UserReadOnlyDTO(
                user.getId().toString(),
                user.getUsername(),
                user.getRole().name()
        );
    }
}
