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
import mkoutra.birthdaykeeper.model.User;
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
            throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException {

        String firstname = friendInsertDTO.getFirstname();
        String lastname = friendInsertDTO.getLastname();
        try {
            User user = userRepository
                    .findUserByUsername(friendInsertDTO.getUsername())
                    .orElseThrow(() -> new EntityNotFoundException("User", "User " + friendInsertDTO.getUsername() + " does not exist."));

            if (friendRepository.findFriendByFirstnameAndLastnameAndUserId(firstname, lastname, user.getId()).isPresent()) {
                throw new EntityAlreadyExistsException("Friend", "Friend:" + firstname + " " + lastname + " already exists.");
            }

            Friend friend = mapper.mapToFriend(friendInsertDTO);

            user.addFriend(friend);                 // Covers both user -> friend and friend -> user relation.
            friend = friendRepository.save(friend);

            /*
            * There is no need to also save the user entity because we changed nothing on user.
            * I added `userRepository.save(user)` and nothing appears on the hibernate logs.
            * On the other hand, if we change user's password and then execute
            * `userRepository.save(user)`, we see in the logs that user is updated.
            *
            * We prefer `friend = friendRepository.save(friend)` because it returns the friend
            *  we the id given by the database.
            * */

            FriendReadOnlyDTO friendReadOnlyDTO = mapper.mapToFriendReadOnlyDTO(friend);
            LOGGER.info("Friend {} {} inserted.", friendReadOnlyDTO.getFirstname(), friendReadOnlyDTO.getLastname());
            return friendReadOnlyDTO;
        } catch (EntityAlreadyExistsException e) {
            LOGGER.error("Friend {} {} already exists.", firstname, lastname);
            throw e;
        } catch (EntityNotFoundException e) {
            LOGGER.error("User with username: {} does not exist. Unable to insert friend.", friendInsertDTO.getUsername());
            throw e;
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public FriendReadOnlyDTO updateFriend(FriendUpdateDTO friendUpdateDTO)
            throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException {
        try {

            User user = userRepository.findUserByUsername(friendUpdateDTO.getUsername())
                    .orElseThrow(() -> new EntityNotFoundException("User", "User " + friendUpdateDTO.getUsername() + " does not exist."));

            if (friendRepository.findById(Long.parseLong(friendUpdateDTO.getId())).isEmpty()) {
                throw new EntityNotFoundException("Friend", "Friend with Id " + friendUpdateDTO.getId() + " does not exist.");
            }

//            if (friendRepository.findFriendByFirstnameAndLastnameAndUserId(
//                    friendUpdateDTO.getFirstname(), friendUpdateDTO.getLastname(), user.getId()).isPresent())
//            {
//                throw new EntityAlreadyExistsException("Friend", "Friend with " + friendUpdateDTO.getFirstname() + " and " + friendUpdateDTO.getLastname() + " already exists.");
//            }

            Friend friend = mapper.mapToFriend(friendUpdateDTO);
            FriendReadOnlyDTO friendReadOnlyDTO = mapper.mapToFriendReadOnlyDTO(friendRepository.save(friend));
            LOGGER.info("Friend {} {} updated.", friendReadOnlyDTO.getFirstname(), friendReadOnlyDTO.getLastname());
            return friendReadOnlyDTO;
        } catch (EntityNotFoundException e) {
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

//    @Override
//    @Transactional(rollbackOn = Exception.class)
//    public List<FriendReadOnlyDTO> getFriendsByDateOfBirthMonth(Short month) {
//        return friendRepository
//                .findFriendsByDateOfBirth_Month(month)
//                .stream()
//                .map(mapper::mapToFriendReadOnlyDTO)
//                .toList();
//    }

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
