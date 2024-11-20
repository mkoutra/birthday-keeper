package mkoutra.birthdaykeeper.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mkoutra.birthdaykeeper.core.exceptions.EntityAlreadyExistsException;
import mkoutra.birthdaykeeper.core.exceptions.EntityInvalidArgumentException;
import mkoutra.birthdaykeeper.core.exceptions.EntityNotFoundException;
import mkoutra.birthdaykeeper.dto.friendDTOs.FriendInsertDTO;
import mkoutra.birthdaykeeper.dto.userDTOs.UserInsertDTO;
import mkoutra.birthdaykeeper.dto.friendDTOs.FriendReadOnlyDTO;
import mkoutra.birthdaykeeper.service.IFriendService;
import mkoutra.birthdaykeeper.service.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/friend")
@RequiredArgsConstructor
public class FriendRestController {

    private final IFriendService friendService;

    // Testing
    @PostMapping("/")
    public ResponseEntity<FriendReadOnlyDTO> insertFriend(
            @Valid @RequestBody FriendInsertDTO friendInsertDTO,
            BindingResult bindingResult) throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException {

        FriendReadOnlyDTO friendReadOnlyDTO = friendService.saveFriend(friendInsertDTO);

        return new ResponseEntity<>(friendReadOnlyDTO, HttpStatus.OK);
    }
}
