package mkoutra.birthdaykeeper.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mkoutra.birthdaykeeper.core.exceptions.EntityAlreadyExistsException;
import mkoutra.birthdaykeeper.core.exceptions.EntityInvalidArgumentException;
import mkoutra.birthdaykeeper.core.exceptions.EntityNotFoundException;
import mkoutra.birthdaykeeper.dto.friendDTOs.FriendInsertDTO;
import mkoutra.birthdaykeeper.dto.friendDTOs.FriendUpdateDTO;
import mkoutra.birthdaykeeper.dto.friendDTOs.FriendReadOnlyDTO;
import mkoutra.birthdaykeeper.service.IFriendService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/")
    public ResponseEntity<FriendReadOnlyDTO> updateFriend(
            @Valid @RequestBody FriendUpdateDTO friendUpdateDTO,
            BindingResult bindingResult
            ) throws EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException{

        FriendReadOnlyDTO friendReadOnlyDTO = friendService.updateFriend(friendUpdateDTO);

        return new ResponseEntity<>(friendReadOnlyDTO, HttpStatus.OK);
    }
}
