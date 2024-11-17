package mkoutra.birthdaykeeper.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mkoutra.birthdaykeeper.core.exceptions.EntityAlreadyExistsException;
import mkoutra.birthdaykeeper.core.exceptions.EntityInvalidArgumentException;
import mkoutra.birthdaykeeper.core.exceptions.EntityNotFoundException;
import mkoutra.birthdaykeeper.dto.friendDTOs.FriendInsertDTO;
import mkoutra.birthdaykeeper.dto.friendDTOs.FriendReadOnlyDTO;
import mkoutra.birthdaykeeper.dto.friendDTOs.FriendUpdateDTO;
import mkoutra.birthdaykeeper.mapper.Mapper;
import mkoutra.birthdaykeeper.model.Friend;
import mkoutra.birthdaykeeper.repository.FriendRepository;
import mkoutra.birthdaykeeper.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FriendService implements IFriendService {
    private final static Logger LOGGER = LoggerFactory.getLogger(FriendService.class);

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
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
    @Transactional(rollbackOn = Exception.class)
    public FriendReadOnlyDTO updateFriend(FriendUpdateDTO friendUpdateDTO)
            throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException {
        try {
            if (userRepository.findUserByUsername(friendUpdateDTO.getUsername()).isEmpty()) {
                throw new EntityNotFoundException("User", "User with username: " + friendUpdateDTO.getUsername() + " does not exist.");
            }

            if (friendRepository.findFriendByUuid(friendUpdateDTO.getId()).isEmpty()) {
                throw new EntityNotFoundException("Friend", "Friend with Id " + friendUpdateDTO.getId() + " does not exist.");
            }
            if (friendRepository.findFriendByFirstnameAndLastname(friendUpdateDTO.getFirstname(), friendUpdateDTO.getLastname()).isPresent()) {
                throw new EntityAlreadyExistsException("Friend", "Friend with " + friendUpdateDTO.getFirstname() + " and " + friendUpdateDTO.getLastname() + " already exists.");
            }

            Friend friend = mapper.mapToFriend(friendUpdateDTO);
            FriendReadOnlyDTO friendReadOnlyDTO = mapper.mapToFriendReadOnlyDTO(friendRepository.save(friend));
            LOGGER.info("Friend {} {} updated.", friendReadOnlyDTO.getFirstname(), friendReadOnlyDTO.getLastname());
            return friendReadOnlyDTO;
        } catch (EntityNotFoundException | EntityAlreadyExistsException e) {
            LOGGER.error("{} Error: {}", e.getCode(), e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public FriendReadOnlyDTO getFriendById(Long id) throws EntityNotFoundException {
        try {
            return friendRepository
                    .findById(id)
                    .map(mapper::mapToFriendReadOnlyDTO)
                    .orElseThrow(() -> new EntityNotFoundException("Friend", "Friend with id " + id + " does not exist."));
        } catch (EntityNotFoundException e) {
            LOGGER.error("Friend Error: Friend with id {} was not found.", id);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public FriendReadOnlyDTO deleteFriend(Long id) throws EntityNotFoundException {
        try {
            FriendReadOnlyDTO friendReadOnlyDTO = friendRepository
                    .findById(id)
                    .map(mapper::mapToFriendReadOnlyDTO)
                    .orElseThrow(() -> new EntityNotFoundException("Friend", "Friend with id " + id + " does not exist."));
            friendRepository.deleteById(id);
            return friendReadOnlyDTO;
        } catch (EntityNotFoundException e) {
            LOGGER.error("Friend Error: Friend with id {} was not deleted.", id);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public List<FriendReadOnlyDTO> getFriendsByDateOfBirthMonth(Short month) {
        return friendRepository
                .findFriendsByDateOfBirth_Month(month)
                .stream()
                .map(mapper::mapToFriendReadOnlyDTO)
                .toList();
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public List<FriendReadOnlyDTO> getAllFriends() {
        return friendRepository
                .findAll()
                .stream()
                .map(mapper::mapToFriendReadOnlyDTO)
                .toList();
    }
}
