package mkoutra.birthdaykeeper.service;

import mkoutra.birthdaykeeper.core.exceptions.EntityAlreadyExistsException;
import mkoutra.birthdaykeeper.core.exceptions.EntityInvalidArgumentException;
import mkoutra.birthdaykeeper.core.exceptions.EntityNotFoundException;
import mkoutra.birthdaykeeper.dto.friendsDTOs.FriendInsertDTO;
import mkoutra.birthdaykeeper.dto.friendsDTOs.FriendReadOnlyDTO;
import mkoutra.birthdaykeeper.dto.friendsDTOs.FriendUpdateDTO;

import java.util.List;

public interface IFriendService {
    FriendReadOnlyDTO saveFriend(FriendInsertDTO friendInsertDTO)
            throws EntityAlreadyExistsException, EntityInvalidArgumentException;

    FriendReadOnlyDTO updateFriend(FriendUpdateDTO friendUpdateDTO)
            throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException;

    FriendReadOnlyDTO deleteFriend(String uuid) throws EntityNotFoundException;
    FriendReadOnlyDTO getFriendById(Integer id) throws EntityNotFoundException;
    List<FriendReadOnlyDTO> getFriendsByDateOfBirthMonth(Integer month);
    List<FriendReadOnlyDTO> getAllFriends();
}
