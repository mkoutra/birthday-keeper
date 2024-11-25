package mkoutra.birthdaykeeper.mapper;

import lombok.RequiredArgsConstructor;
import mkoutra.birthdaykeeper.dto.friendDTOs.FriendInsertDTO;
import mkoutra.birthdaykeeper.dto.friendDTOs.FriendReadOnlyDTO;
import mkoutra.birthdaykeeper.dto.friendDTOs.FriendUpdateDTO;
import mkoutra.birthdaykeeper.dto.userDTOs.UserInsertDTO;
import mkoutra.birthdaykeeper.dto.userDTOs.UserReadOnlyDTO;
import mkoutra.birthdaykeeper.model.Friend;
import mkoutra.birthdaykeeper.model.User;
import mkoutra.birthdaykeeper.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class Mapper {

    private final UserRepository userRepository;

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
        user.setPassword("Hashed: " + userInsertDTO.getPassword()); // TODO: Add Hashing
        return user;
    }

    public UserReadOnlyDTO mapToUserReadOnlyDTO(User user) {
        return new UserReadOnlyDTO(
                user.getId().toString(),
                user.getUsername()
        );
    }
}
