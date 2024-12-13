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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

        User loggedInUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String firstname = friendInsertDTO.getFirstname();
        String lastname = friendInsertDTO.getLastname();

        try {
            User user = userRepository
                    .findUserByUsername(loggedInUser.getUsername())
                    .orElseThrow(() -> new EntityNotFoundException("User", "User " + loggedInUser.getUsername() + " does not exist."));

            if (friendRepository.findFriendByFirstnameAndLastnameAndUserId(firstname, lastname, user.getId()).isPresent()) {
                throw new EntityAlreadyExistsException("Friend", "Friend:" + firstname + " " + lastname + " already exists.");
            }

            Friend friend = mapper.mapToFriend(friendInsertDTO);

            user.addFriend(friend);                     // Covers both user -> friend and friend -> user relation.
            friend = friendRepository.save(friend);

            /*
            * There is no need to also save the user entity because we changed nothing on user.
            * I added `userRepository.save(user)` and nothing appears on the hibernate logs.
            * On the other hand, if we change user's password and then execute
            * `userRepository.save(user)`, we see in the logs that user is updated.
            *
            * We prefer `friend = friendRepository.save(friend)` because it returns the friend
            * with the id given by the database.
            * */

            FriendReadOnlyDTO friendReadOnlyDTO = mapper.mapToFriendReadOnlyDTO(friend);
            LOGGER.info("Friend {} {} inserted.", friendReadOnlyDTO.getFirstname(), friendReadOnlyDTO.getLastname());
            return friendReadOnlyDTO;
        } catch (EntityAlreadyExistsException e) {
            LOGGER.error("Friend {} {} already exists.", firstname, lastname);
            throw e;
        } catch (EntityNotFoundException e) {
            LOGGER.error("User with username: {} does not exist. Unable to insert friend.", loggedInUser.getUsername());
            throw e;
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public FriendReadOnlyDTO updateFriend(FriendUpdateDTO friendUpdateDTO)
            throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException {
        try {
            // Check if user exists
            User user = userRepository.findUserByUsername(friendUpdateDTO.getUsername())
                    .orElseThrow(() -> new EntityNotFoundException("User", "User " + friendUpdateDTO.getUsername() + " does not exist."));

            Optional<Friend> friendToBeUpdated = friendRepository.findById(Long.parseLong(friendUpdateDTO.getId()));
            // Check if a friend with the Id given in the DTO exists
            if (friendToBeUpdated.isEmpty()) {
                throw new EntityNotFoundException("Friend", "Friend with Id " + friendUpdateDTO.getId() + " does not exist.");
            }

            Friend friend = mapper.mapToFriend(friendUpdateDTO);
            friend.setUser(user);

            // Check if a user already has a different friend with the given firstname, lastname
            Optional<Friend> friendCheck = friendRepository.findFriendByFirstnameAndLastnameAndUserId(
                    friend.getFirstname(), friend.getLastname(), user.getId());

            if (friendCheck.isPresent() && !Objects.equals(friendCheck.get().getId(), friend.getId())) {
                throw new EntityAlreadyExistsException("Friend",
                        "The user: " + user.getUsername() + " already has a friend with" +
                        "Firstname: " + friend.getFirstname() +
                        " and Lastname: " + friend.getLastname());
            }

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
            Friend friendToDelete = friendRepository
                    .findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Friend", "Friend with id " + id + " does not exist."));

            FriendReadOnlyDTO friendReadOnlyDTO = mapper.mapToFriendReadOnlyDTO(friendToDelete);

            friendToDelete.getUser().removeFriend(friendToDelete);
            friendRepository.deleteById(id);

            return friendReadOnlyDTO;
        } catch (EntityNotFoundException e) {
            LOGGER.error("Friend Error: Friend with id {} was not deleted.", id);
            throw e;
        }
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

    @Override
    @Transactional(rollbackOn = Exception.class)
    public List<FriendReadOnlyDTO> getAllFriendsForUser(String username) throws EntityNotFoundException {
        try {
            User user = userRepository
                    .findUserByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("User", "User with username: " + username + " does not exist."));
            return friendRepository.findFriendsByUserId(user.getId()).stream().map(mapper::mapToFriendReadOnlyDTO).toList();
        } catch (EntityNotFoundException e) {
            LOGGER.error("User with username {} does not exist.", username);
            throw e;
        }
    }
}
