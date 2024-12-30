package mkoutra.birthdaykeeper.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mkoutra.birthdaykeeper.core.Paginated;
import mkoutra.birthdaykeeper.core.exceptions.EntityAlreadyExistsException;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FriendService implements IFriendService {
    private final static Logger LOGGER = LoggerFactory.getLogger(FriendService.class);

    private final FriendRepository friendRepository;
    private final UserRepository userRepository;
    private final Mapper mapper;

    /**
     * Checks if the user with the given username has a friend with the specified ID.
     *
     * @param friendId  The friend's ID.
     * @param username  The user's username.
     * @return          True if the friend exists for the user, otherwise false.
     */
    @Transactional(rollbackOn = Exception.class)
    @Override
    public boolean existsFriendIdToUsername(Long friendId, String username) {
        return friendRepository.existsByFriendIdAndUsername(friendId, username);
    }

    /**
     * Adds a new friend to the user identified by the given username.
     *
     * @param friendInsertDTO               The friend's details.
     * @param username                      The user's username.
     * @return                              Details of the saved friend.
     * @throws EntityAlreadyExistsException If a friend with the same name exists for the user.
     * @throws EntityNotFoundException      If the user is not found.
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public FriendReadOnlyDTO saveFriend(FriendInsertDTO friendInsertDTO, String username)
            throws EntityAlreadyExistsException, EntityNotFoundException {

        String firstname = friendInsertDTO.getFirstname();
        String lastname = friendInsertDTO.getLastname();

        try {
            User user = userRepository
                    .findUserByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("User", "User " + username + " does not exist."));

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
            LOGGER.error("User with username: {} does not exist. Unable to insert friend.", username);
            throw e;
        }
    }

    /**
     * Updates a friend's details based on the provided DTO.
     *
     * @param friendUpdateDTO               The DTO containing updated friend information.
     * @return                              The updated friend's details.
     * @throws EntityAlreadyExistsException If the updated details match an existing friend.
     * @throws EntityNotFoundException      If the friend ID does not exist.
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public FriendReadOnlyDTO updateFriend(FriendUpdateDTO friendUpdateDTO)
            throws EntityAlreadyExistsException, EntityNotFoundException {
        try {
            Friend friendToUpdate = friendRepository
                    .findById(Long.parseLong(friendUpdateDTO.getId()))
                    .orElseThrow( () -> new EntityNotFoundException("Friend",
                                    "Friend with Id " + friendUpdateDTO.getId() + " does not exist."));
            User user = friendToUpdate.getUser();    // The user that the friend belongs to

            Friend updatedFriend = mapper.mapToFriend(friendUpdateDTO);
            updatedFriend.setUser(user);

            // Check if the given names already exist for a different friend of the current user.
            boolean friendAlreadyExists = friendRepository
                    .findFriendByFirstnameAndLastnameAndUserId(
                            updatedFriend.getFirstname(), updatedFriend.getLastname(), user.getId())
                    .stream()
                    .anyMatch(friend -> !friend.getId().equals(updatedFriend.getId()));

            if (friendAlreadyExists) {
                throw new EntityAlreadyExistsException("Friend",
                        new StringBuilder("The user: ")
                                .append(user.getUsername())
                                .append(" already has a friend with Firstname: ")
                                .append(updatedFriend.getFirstname())
                                .append(" and Lastname: ")
                                .append(updatedFriend.getLastname())
                                .toString());
            }

            FriendReadOnlyDTO friendReadOnlyDTO = mapper.mapToFriendReadOnlyDTO(friendRepository.save(updatedFriend));
            LOGGER.info("Friend {} {} updated.", friendReadOnlyDTO.getFirstname(), friendReadOnlyDTO.getLastname());
            return friendReadOnlyDTO;
        } catch (EntityNotFoundException e) {
            LOGGER.error("{} Error: {}", e.getCode(), e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieves a friend's details by their ID.
     *
     * @param id                        The friend's ID.
     * @return                          The friend's details as a DTO.
     * @throws EntityNotFoundException  If no friend is found with the given ID.
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public FriendReadOnlyDTO getFriendById(Long id) throws EntityNotFoundException {
        try {
            return friendRepository
                    .findById(id)
                    .map(mapper::mapToFriendReadOnlyDTO)
                    .orElseThrow(() -> new EntityNotFoundException("Friend",
                            "Friend with id: " + id + " does not exist."));
        } catch (EntityNotFoundException e) {
            LOGGER.error("Friend Error: Friend with id {} was not found.", id);
            throw e;
        }
    }

    /**
     * Deletes a friend by their ID and returns their details.
     *
     * @param id                        The friend's ID.
     * @return                          A DTO with the deleted friend's details.
     * @throws EntityNotFoundException  If no friend is found with the given ID.
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public FriendReadOnlyDTO deleteFriend(Long id) throws EntityNotFoundException {
        try {
            Friend friendToDelete = friendRepository
                    .findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Friend",
                            "Friend with id " + id + " does not exist."));

            FriendReadOnlyDTO friendReadOnlyDTO = mapper.mapToFriendReadOnlyDTO(friendToDelete);

            friendToDelete.getUser().removeFriend(friendToDelete);
            friendRepository.deleteById(id);
            LOGGER.info("Friend: {} {} deleted successfully.",
                    friendToDelete.getFirstname(), friendToDelete.getLastname());
            return friendReadOnlyDTO;
        } catch (EntityNotFoundException e) {
            LOGGER.error("Friend Error: Friend with id {} was not deleted.", id);
            throw e;
        }
    }

    /**
     * Retrieves all friends in the database.
     *
     * @return A list of DTOs with each friend's details.
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public List<FriendReadOnlyDTO> getAllFriends() {
        return friendRepository
                .findAll()
                .stream()
                .map(mapper::mapToFriendReadOnlyDTO)
                .toList();
    }

    /**
     * Retrieves all friends of the specified user.
     *
     * @param username                  The user's username.
     * @return                          A list of DTOs with the friends' details.
     * @throws EntityNotFoundException  If no user is found with the given username.
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public List<FriendReadOnlyDTO> getAllFriendsForUser(String username) throws EntityNotFoundException {
        try {
            User user = userRepository
                    .findUserByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("User",
                            "User with username: " + username + " does not exist."));

            return friendRepository
                    .findFriendsByUserId(user.getId())
                    .stream()
                    .map(mapper::mapToFriendReadOnlyDTO)
                    .toList();
        } catch (EntityNotFoundException e) {
            LOGGER.error("User with username {} does not exist.", username);
            throw e;
        }
    }

    /**
     * Retrieves a friend's details by ID, ensuring they belong to the specified user.
     *
     * @param id                        The friend's ID.
     * @param username                  The owner's username.
     * @return                          The friend's details as a DTO.
     * @throws EntityNotFoundException  If the user does not have a friend with the given ID.
     */
    @Transactional(rollbackOn = Exception.class)
    @Override
    public FriendReadOnlyDTO getFriendByIdAndUsername(Long id, String username)
            throws EntityNotFoundException {
        try {
            // Check if the given id belongs to the principal user
            Optional<Friend> friend = userRepository
                    .findUserByUsername(username)
                    .orElseThrow(() -> new EntityNotFoundException("User", "User "+ username + " not found."))
                    .getAllFriends()
                    .stream()
                    .filter(fr -> fr.getId().equals(id))
                    .findFirst();

            return friend
                    .map(mapper::mapToFriendReadOnlyDTO)
                    .orElseThrow(() -> new EntityNotFoundException("Friend",
                            "User " + username + " does not have a friend with id " + id));
        }
        catch (EntityNotFoundException e) {
            LOGGER.error(e.getMessage());
            throw e;
        }
    }

    /**
     * Retrieve the page of friends for the user with the specified ID.
     *
     * @param pageNo    The page number, starting from 0.
     * @param size      The number of elements per page.
     * @param userId    The ID of the user whose friends are to be retrieved.
     * @return          A {@link Paginated} object containing FriendReadOnlyDTO instances
     *                  and selected page information.
     */
    @Transactional(rollbackOn = Exception.class)
    @Override
    public Paginated<FriendReadOnlyDTO> getPaginatedFriends(int pageNo, int size, Long userId) {
        String defaultSort = "id";
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by(defaultSort).ascending());
        return new Paginated<>(friendRepository.findFriendsByUserId(userId, pageable).map(mapper::mapToFriendReadOnlyDTO));
    }
}
