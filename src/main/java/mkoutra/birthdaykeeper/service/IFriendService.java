package mkoutra.birthdaykeeper.service;

import mkoutra.birthdaykeeper.core.exceptions.EntityAlreadyExistsException;
import mkoutra.birthdaykeeper.core.exceptions.EntityInvalidArgumentException;
import mkoutra.birthdaykeeper.core.exceptions.EntityNotFoundException;
import mkoutra.birthdaykeeper.dto.friendDTOs.FriendInsertDTO;
import mkoutra.birthdaykeeper.dto.friendDTOs.FriendReadOnlyDTO;
import mkoutra.birthdaykeeper.dto.friendDTOs.FriendUpdateDTO;

import java.util.List;

public interface IFriendService {
    FriendReadOnlyDTO saveFriend(FriendInsertDTO friendInsertDTO)
            throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException;

    FriendReadOnlyDTO updateFriend(FriendUpdateDTO friendUpdateDTO)
            throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException;

    FriendReadOnlyDTO getFriendById(Long id) throws EntityNotFoundException;
    FriendReadOnlyDTO deleteFriend(Long id) throws EntityNotFoundException;
//    List<FriendReadOnlyDTO> getFriendsByDateOfBirthMonth(Short month);
    List<FriendReadOnlyDTO> getAllFriends();
}
