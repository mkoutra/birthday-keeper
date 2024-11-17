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
        User user =  userRepository.findUserByUsername(dto.getUsername()).orElse(null);

        return new Friend(null, dto.getFirstname(), dto.getLastname(), dto.getNickname(),
                dto.getDateOfBirth(), user);
    }

    public Friend mapToFriend(FriendUpdateDTO dto) {
        User user =  userRepository.findUserByUsername(dto.getUsername()).orElse(null);

        return new Friend(
                Long.parseLong(dto.getId()),
                dto.getFirstname(),
                dto.getLastname(),
                dto.getNickname(),
                dto.getDateOfBirth(),
                user
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
        return new User(
                null,
                userInsertDTO.getUsername(),
                "Hashed: " + userInsertDTO.getPassword(), // TODO: Add Hashing
                null
        );
    }

    public UserReadOnlyDTO mapToUserReadOnlyDTO(User user) {
        return new UserReadOnlyDTO(
                user.getId().toString(),
                user.getUsername()
        );
    }
}
