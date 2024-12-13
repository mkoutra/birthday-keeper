package mkoutra.birthdaykeeper.service;

import mkoutra.birthdaykeeper.core.exceptions.EntityAlreadyExistsException;
import mkoutra.birthdaykeeper.core.exceptions.EntityInvalidArgumentException;
import mkoutra.birthdaykeeper.core.exceptions.EntityNotFoundException;
import mkoutra.birthdaykeeper.dto.friendDTOs.FriendInsertDTO;
import mkoutra.birthdaykeeper.dto.friendDTOs.FriendReadOnlyDTO;
import mkoutra.birthdaykeeper.dto.friendDTOs.FriendUpdateDTO;
import mkoutra.birthdaykeeper.model.Friend;

import java.util.List;

public interface IFriendService {
    FriendReadOnlyDTO saveFriend(FriendInsertDTO friendInsertDTO, String username)
            throws EntityAlreadyExistsException, EntityNotFoundException;

    FriendReadOnlyDTO updateFriend(FriendUpdateDTO friendUpdateDTO)
            throws EntityAlreadyExistsException, EntityNotFoundException;

    FriendReadOnlyDTO getFriendById(Long id) throws EntityNotFoundException;
    FriendReadOnlyDTO deleteFriend(Long id) throws EntityNotFoundException;
    List<FriendReadOnlyDTO> getAllFriends();
    List<FriendReadOnlyDTO> getAllFriendsForUser(String username) throws EntityNotFoundException;
    FriendReadOnlyDTO getFriendByIdAndUsername(Long id, String username) throws EntityNotFoundException;
    boolean existsFriendIdToUsername(Long friendId, String username);
}