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
import mkoutra.birthdaykeeper.service.IFriendService;
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

//    @GetMapping("/")
//    public ResponseEntity<List<FriendReadOnlyDTO>> getAllFriends() {
//        return new ResponseEntity<>(friendService.getAllFriends(), HttpStatus.OK);
//    }

    @GetMapping("/{id}")
    public ResponseEntity<FriendReadOnlyDTO> getFriendWithId(@PathVariable String id)
            throws EntityNotFoundException {
        return new ResponseEntity<>(friendService.getFriendById(Long.parseLong(id)), HttpStatus.OK);
    }

    // Get All Friends
    @GetMapping("/")
    public ResponseEntity<List<FriendReadOnlyDTO>> getFriendsFromUser(@RequestParam("username") String username)
            throws EntityNotFoundException {
        if (username.isBlank()) {
            return new ResponseEntity<>(friendService.getAllFriends(), HttpStatus.OK);
        }
        return new ResponseEntity<>(friendService.getAllFriendsForUser(username), HttpStatus.OK);
    }

//    @PostMapping("/")
//    public ResponseEntity<FriendReadOnlyDTO> insertFriend(
//            @Valid @RequestBody FriendInsertDTO friendInsertDTO,
//            BindingResult bindingResult) throws ValidationException, EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException {
//
//        if (bindingResult.hasErrors()) {
//            throw new ValidationException(bindingResult);
//        }
//
//        FriendReadOnlyDTO friendReadOnlyDTO = friendService.saveFriend(friendInsertDTO);
//
//        return new ResponseEntity<>(friendReadOnlyDTO, HttpStatus.OK);
//    }

    @PostMapping("/")
    public ResponseEntity<FriendReadOnlyDTO> insertFriend(
            @AuthenticationPrincipal User loggedInUser,
            @Valid @RequestBody FriendInsertDTO friendInsertDTO,
            BindingResult bindingResult) throws ValidationException, EntityAlreadyExistsException, EntityInvalidArgumentException, EntityNotFoundException {

        if (loggedInUser == null || !loggedInUser.isEnabled()) {
            throw new EntityInvalidArgumentException("User", "Invalid user.");
        }

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        LOGGER.info("Username {} wants to insert friend with surname {}", loggedInUser.getUsername(), friendInsertDTO.getLastname());
        FriendReadOnlyDTO friendReadOnlyDTO = friendService.saveFriend(friendInsertDTO);

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
