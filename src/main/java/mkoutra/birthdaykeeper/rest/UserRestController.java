package mkoutra.birthdaykeeper.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mkoutra.birthdaykeeper.core.exceptions.EntityAlreadyExistsException;
import mkoutra.birthdaykeeper.dto.userDTOs.UserInsertDTO;
import mkoutra.birthdaykeeper.dto.userDTOs.UserReadOnlyDTO;
import mkoutra.birthdaykeeper.service.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserRestController {

    private final IUserService userService;

    @PostMapping("/")
    public ResponseEntity<UserReadOnlyDTO> insertUser(
            @Valid @RequestBody UserInsertDTO userInsertDTO,
            BindingResult bindingResult) throws EntityAlreadyExistsException {
        UserReadOnlyDTO userReadOnlyDTO = userService.saveUser(userInsertDTO);
        return new ResponseEntity<>(userReadOnlyDTO, HttpStatus.OK);
    }
}
