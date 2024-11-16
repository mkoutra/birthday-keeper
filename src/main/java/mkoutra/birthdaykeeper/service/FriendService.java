package mkoutra.birthdaykeeper.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mkoutra.birthdaykeeper.core.exceptions.EntityAlreadyExistsException;
import mkoutra.birthdaykeeper.core.exceptions.EntityInvalidArgumentException;
import mkoutra.birthdaykeeper.core.exceptions.EntityNotFoundException;
import mkoutra.birthdaykeeper.dto.friendsDTOs.FriendInsertDTO;
import mkoutra.birthdaykeeper.dto.friendsDTOs.FriendReadOnlyDTO;
import mkoutra.birthdaykeeper.dto.friendsDTOs.FriendUpdateDTO;
import mkoutra.birthdaykeeper.mapper.Mapper;
import mkoutra.birthdaykeeper.model.Friend;
import mkoutra.birthdaykeeper.repository.FriendRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService implements IFriendService {
    private final static Logger LOGGER = LoggerFactory.getLogger(FriendService.class);

    private final FriendRepository friendRepository;
    private final Mapper mapper;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public FriendReadOnlyDTO saveFriend(FriendInsertDTO friendInsertDTO)
            throws EntityAlreadyExistsException, EntityInvalidArgumentException {

        String firstname = friendInsertDTO.getFirstname();
        String lastname = friendInsertDTO.getLastname();

        try {
            if (friendRepository.findFriendByFirstnameAndLastname(firstname, lastname).isPresent()) {
                throw new EntityAlreadyExistsException("Friend", "Friend:" + firstname + " " + lastname + " already exists.");
            }

            Friend friend = mapper.mapToFriend(friendInsertDTO);
            FriendReadOnlyDTO friendReadOnlyDTO = mapper.mapToFriendReadOnlyDTO(friendRepository.save(friend));
            LOGGER.info("Friend {} {} inserted.", friendReadOnlyDTO.getFirstname(), friendReadOnlyDTO.getLastname());

            return friendReadOnlyDTO;
        } catch (EntityAlreadyExistsException e) {
            LOGGER.error("Friend {} {} already exists.", firstname, lastname);
            throw e;
        }
    }

    @Override
    public FriendReadOnlyDTO updateFriend(FriendUpdateDTO friendUpdateDTO)
            throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException {
        try {
            if (friendRepository.findFriendByUuid(friendUpdateDTO.getUuid()).isEmpty()) {
                throw new EntityNotFoundException("Friend", "Friend with uuid " + friendUpdateDTO.getUuid() + " does not exist.");
            }
            if (friendRepository.findFriendByFirstnameAndLastname(friendUpdateDTO.getFirstname(), friendUpdateDTO.getLastname()).isPresent()) {
                throw new EntityAlreadyExistsException("Friend", "Friend with " + friendUpdateDTO.getFirstname() + " and " + friendUpdateDTO.getLastname() + " already exists.");
            }
        } catch (EntityNotFoundException e) {
            LOGGER.error("Friend with uuid {} does not exist.", friendUpdateDTO.getUuid());
            throw e;
        } catch (EntityAlreadyExistsException e) {
            LOGGER.error("Friend with {} and {} already exists.", friendUpdateDTO.getFirstname(), friendUpdateDTO.getLastname());
            throw e;
        }
    }

    @Override
    public FriendReadOnlyDTO deleteFriend(String uuid) throws EntityNotFoundException {
        return null;
    }

    @Override
    public FriendReadOnlyDTO getFriendById(Integer id) throws EntityNotFoundException {
        return null;
    }

    @Override
    public List<FriendReadOnlyDTO> getFriendsByDateOfBirthMonth(Integer month) {
        return List.of();
    }

    @Override
    public List<FriendReadOnlyDTO> getAllFriends() {
        return List.of();
    }
}
