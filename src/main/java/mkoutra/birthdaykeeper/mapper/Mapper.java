package mkoutra.birthdaykeeper.mapper;

import lombok.NoArgsConstructor;
import mkoutra.birthdaykeeper.dto.friendsDTOs.FriendInsertDTO;
import mkoutra.birthdaykeeper.dto.friendsDTOs.FriendReadOnlyDTO;
import mkoutra.birthdaykeeper.dto.friendsDTOs.FriendUpdateDTO;
import mkoutra.birthdaykeeper.model.Friend;
import org.springframework.stereotype.Component;

@NoArgsConstructor
@Component
public class Mapper {
    public Friend mapToFriend(FriendInsertDTO dto) {
        return new Friend(null, dto.getFirstname(), dto.getLastname(), dto.getNickname(), dto.getDateOfBirth());
    }

    public Friend mapToFriend(FriendUpdateDTO dto) {
        return new Friend(null, dto.getFirstname(), dto.getLastname(), dto.getNickname(), dto.getDateOfBirth());
    }

    public Friend mapToFriend(FriendReadOnlyDTO dto) {
        return new Friend(null, dto.getFirstname(), dto.getLastname(), dto.getNickname(), dto.getDateOfBirth());
    }

    public FriendReadOnlyDTO mapToFriendReadOnlyDTO(Friend friend) {
        return new FriendReadOnlyDTO(
                friend.getUuid(),
                friend.getFirstname(),
                friend.getLastname(),
                friend.getNickname(),
                friend.getDateOfBirth()
        );
    }
}
