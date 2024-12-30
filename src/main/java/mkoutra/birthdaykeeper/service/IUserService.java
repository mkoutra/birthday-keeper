package mkoutra.birthdaykeeper.service;

import mkoutra.birthdaykeeper.core.Paginated;
import mkoutra.birthdaykeeper.core.exceptions.EntityAlreadyExistsException;
import mkoutra.birthdaykeeper.core.exceptions.EntityNotFoundException;
import mkoutra.birthdaykeeper.dto.userDTOs.UserChangePasswordDTO;
import mkoutra.birthdaykeeper.dto.userDTOs.UserInsertDTO;
import mkoutra.birthdaykeeper.dto.userDTOs.UserReadOnlyDTO;

import java.util.List;

public interface IUserService {
    UserReadOnlyDTO saveUser(UserInsertDTO userInsertDTO) throws EntityAlreadyExistsException;
    UserReadOnlyDTO deleteUser(Long id) throws EntityNotFoundException;
    List<UserReadOnlyDTO> getAllUsers();
    Paginated<UserReadOnlyDTO> getPaginatedUsers(int pageNo, int size);
    UserReadOnlyDTO overrideUserPassword(Long id, String newPassword) throws EntityNotFoundException;
    UserReadOnlyDTO changeUserPassword(UserChangePasswordDTO changePasswordDTO) throws EntityNotFoundException;
}
