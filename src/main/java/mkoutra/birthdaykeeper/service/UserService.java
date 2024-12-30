package mkoutra.birthdaykeeper.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mkoutra.birthdaykeeper.core.Paginated;
import mkoutra.birthdaykeeper.core.exceptions.EntityAlreadyExistsException;
import mkoutra.birthdaykeeper.core.exceptions.EntityNotFoundException;
import mkoutra.birthdaykeeper.dto.userDTOs.UserChangePasswordDTO;
import mkoutra.birthdaykeeper.dto.userDTOs.UserInsertDTO;
import mkoutra.birthdaykeeper.dto.userDTOs.UserReadOnlyDTO;
import mkoutra.birthdaykeeper.mapper.Mapper;
import mkoutra.birthdaykeeper.model.User;
import mkoutra.birthdaykeeper.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final Mapper mapper;
    private final PasswordEncoder passwordEncoder;


    @Override
    public List<UserReadOnlyDTO> getAllUsers() {
        return userRepository
                .findAll()
                .stream()
                .map(mapper::mapToUserReadOnlyDTO)
                .toList();
    }

    @Override
    public Paginated<UserReadOnlyDTO> getPaginatedUsers(int pageNo, int size) {
        String defaultSorting = "id";
        Pageable pageable = PageRequest.of(pageNo, size, Sort.by(defaultSorting).ascending());
        return new Paginated<>(userRepository.findAll(pageable).map(mapper::mapToUserReadOnlyDTO));
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserReadOnlyDTO saveUser(UserInsertDTO userInsertDTO) throws EntityAlreadyExistsException {
        try {
            if (userRepository.findUserByUsername(userInsertDTO.getUsername()).isPresent()) {
                throw new EntityAlreadyExistsException("User",
                        "User with username " + userInsertDTO.getUsername() + " already exists.");
            }
            User user = mapper.mapToUser(userInsertDTO);
            UserReadOnlyDTO userReadOnlyDTO = mapper.mapToUserReadOnlyDTO(userRepository.save(user));

            LOGGER.info("User with id {} and username {} inserted.",
                    userReadOnlyDTO.getId() ,userInsertDTO.getUsername());
            return userReadOnlyDTO;
        } catch(EntityAlreadyExistsException e) {
            LOGGER.error("User Error: User with username {} does not exist.", userInsertDTO.getUsername());
            throw e;
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserReadOnlyDTO deleteUser(Long id) throws EntityNotFoundException {
        try {
            UserReadOnlyDTO userToDeleteDTO = userRepository
                    .findById(id)
                    .map(mapper::mapToUserReadOnlyDTO)
                    .orElseThrow(() -> new EntityNotFoundException("User",
                            "User with id " + id + " was not found"));
            userRepository.deleteById(id);
            LOGGER.info("User with id: {} and all their friends deleted successfully.", id);
            return userToDeleteDTO;
        } catch(EntityNotFoundException e) {
            LOGGER.error("User Error: User with id {} does not exist.", id);
            throw  e;
        }
    }

    /**
     * Updates the password for the user with the specified ID.
     *
     * @param id                        The ID of the user whose password is to be updated.
     * @param newPassword               The new password to set for the user.
     * @return                          A DTO containing the updated user's information.
     * @throws EntityNotFoundException  If no user with the specified ID exists.
     */
    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserReadOnlyDTO overrideUserPassword(Long id, String newPassword) throws EntityNotFoundException {
        try {
            User user = userRepository
                    .findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("User",
                            "User with id " + id + " was not found"));

            user.setPassword(passwordEncoder.encode(newPassword));
            User userWithNewPassword = userRepository.save(user);

            return mapper.mapToUserReadOnlyDTO(userWithNewPassword);
        } catch (EntityNotFoundException e) {
            LOGGER.error("User Error: User with id {} does not exist.", id);
            throw  e;
        }
    }

    // TODO
    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserReadOnlyDTO changeUserPassword(UserChangePasswordDTO changePasswordDTO) throws EntityNotFoundException {
        return null;
    }
}
