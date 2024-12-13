package mkoutra.birthdaykeeper.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mkoutra.birthdaykeeper.core.exceptions.EntityAlreadyExistsException;
import mkoutra.birthdaykeeper.core.exceptions.EntityInvalidArgumentException;
import mkoutra.birthdaykeeper.core.exceptions.EntityNotFoundException;
import mkoutra.birthdaykeeper.core.exceptions.ValidationException;
import mkoutra.birthdaykeeper.dto.friendDTOs.FriendInsertDTO;
import mkoutra.birthdaykeeper.dto.friendDTOs.FriendUpdateDTO;
import mkoutra.birthdaykeeper.dto.friendDTOs.FriendReadOnlyDTO;
import mkoutra.birthdaykeeper.model.User;
import mkoutra.birthdaykeeper.repository.UserRepository;
import mkoutra.birthdaykeeper.service.IFriendService;
import mkoutra.birthdaykeeper.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
public class FriendRestController {

    private final static Logger LOGGER = LoggerFactory.getLogger(FriendRestController.class);
    private final IFriendService friendService;
    private final UserRepository userRepository;

    // Get a friend with the id given, for the user in the security context.
    @GetMapping("/{id}")
    public ResponseEntity<FriendReadOnlyDTO> getFriendForUser(
            @AuthenticationPrincipal User loggedInUser,
            @PathVariable String id)
            throws EntityNotFoundException, EntityInvalidArgumentException {

        if (loggedInUser == null || !loggedInUser.isEnabled()) {
            throw new EntityInvalidArgumentException("User", "Invalid user.");
        }

        FriendReadOnlyDTO friendReadOnlyDTO = friendService.getFriendByIdAndUsername(Long.parseLong(id), loggedInUser.getUsername());

        return new ResponseEntity<>(friendReadOnlyDTO, HttpStatus.OK);
    }

    // Get all friends of the user which is inside the security context.
    @GetMapping("/")
    public ResponseEntity<List<FriendReadOnlyDTO>> getFriendsForUser(@AuthenticationPrincipal User loggedInUser)
            throws EntityInvalidArgumentException, EntityNotFoundException {

        if (loggedInUser == null || !loggedInUser.isEnabled()) {
            throw new EntityInvalidArgumentException("User", "Invalid user.");
        }

        List<FriendReadOnlyDTO> allUserFriends = friendService.getAllFriendsForUser(loggedInUser.getUsername());

        return new ResponseEntity<>(allUserFriends, HttpStatus.OK);
    }


    // Insert a Friend to the user which is inside the securityContext.
    @PostMapping("/")
    public ResponseEntity<FriendReadOnlyDTO> insertFriend(
            @AuthenticationPrincipal User loggedInUser,
            @Valid @RequestBody FriendInsertDTO friendInsertDTO,
            BindingResult bindingResult)
            throws ValidationException, EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException {

        if (loggedInUser == null || !loggedInUser.isEnabled()) {
            throw new EntityInvalidArgumentException("User", "Invalid user.");
        }

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        LOGGER.info("Username {} wants to insert friend with surname {}", loggedInUser.getUsername(), friendInsertDTO.getLastname());
        FriendReadOnlyDTO friendReadOnlyDTO = friendService.saveFriend(friendInsertDTO, loggedInUser.getUsername());

        return new ResponseEntity<>(friendReadOnlyDTO, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FriendReadOnlyDTO> updateFriend(
            @PathVariable String id,    // TODO Use it
            @Valid @RequestBody FriendUpdateDTO friendUpdateDTO,
            BindingResult bindingResult
            ) throws ValidationException, EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException{

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        FriendReadOnlyDTO friendReadOnlyDTO = friendService.updateFriend(friendUpdateDTO);

        return new ResponseEntity<>(friendReadOnlyDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<FriendReadOnlyDTO> deleteFriendWithId(@PathVariable String id)
            throws EntityNotFoundException {
        return new ResponseEntity<>(friendService.deleteFriend(Long.parseLong(id)), HttpStatus.OK);
    }
}
