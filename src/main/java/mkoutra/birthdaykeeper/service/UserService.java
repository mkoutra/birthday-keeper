package mkoutra.birthdaykeeper.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import mkoutra.birthdaykeeper.core.exceptions.EntityAlreadyExistsException;
import mkoutra.birthdaykeeper.dto.userDTOs.UserInsertDTO;
import mkoutra.birthdaykeeper.dto.userDTOs.UserReadOnlyDTO;
import mkoutra.birthdaykeeper.mapper.Mapper;
import mkoutra.birthdaykeeper.model.User;
import mkoutra.birthdaykeeper.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final Mapper mapper;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserReadOnlyDTO saveUser(UserInsertDTO userInsertDTO) throws EntityAlreadyExistsException {
        try {
            if (userRepository.findUserByUsername(userInsertDTO.getUsername()).isPresent()) {
                throw new EntityAlreadyExistsException("User", "User with username " + userInsertDTO.getUsername() + " already exists.");
            }
            User user = mapper.mapToUser(userInsertDTO);
            UserReadOnlyDTO userReadOnlyDTO = mapper.mapToUserReadOnlyDTO(userRepository.save(user));

            LOGGER.info("User with id {} and username {} inserted.", userReadOnlyDTO.getId() ,userInsertDTO.getUsername());
            return userReadOnlyDTO;
        } catch(EntityAlreadyExistsException e) {
            LOGGER.error("User Error: User with username {} does not exist.", userInsertDTO.getUsername());
            throw e;
        }
    }
}
