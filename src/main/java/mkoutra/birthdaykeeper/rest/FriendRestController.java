package mkoutra.birthdaykeeper.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mkoutra.birthdaykeeper.core.Paginated;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
@RequiredArgsConstructor
@SecurityRequirement(name = "jwtAuth")
@Tag(name = "Friend actions Controller",
        description = "All methods use the authenticated user which is inside the security context.")
public class FriendRestController {

    private final static Logger LOGGER = LoggerFactory.getLogger(FriendRestController.class);
    private final IFriendService friendService;

    @Operation(summary = "Retrieve a friend by ID for the authenticated user.")
    @GetMapping("/{id}")
    public ResponseEntity<FriendReadOnlyDTO> getFriendForUser(
            @AuthenticationPrincipal User loggedInUser,
            @PathVariable String id)
            throws EntityNotFoundException, AccessDeniedException {

        if (loggedInUser == null || !loggedInUser.isEnabled()) {
            throw new AccessDeniedException("User is either not authenticated or disabled.");
        }

        if (!friendService.existsFriendIdToUsername(Long.parseLong(id), loggedInUser.getUsername())) {
            throw new EntityNotFoundException("Friend", "User " + loggedInUser.getUsername()
                    + " does not have a friend with id " + id);
        }

        FriendReadOnlyDTO friendReadOnlyDTO = friendService.getFriendById(Long.parseLong(id));

        return new ResponseEntity<>(friendReadOnlyDTO, HttpStatus.OK);
    }

    @Operation(summary = "Get all friends of the authenticated user.")
    @GetMapping
    public ResponseEntity<List<FriendReadOnlyDTO>> getFriendsForUser(
            @AuthenticationPrincipal User loggedInUser)
            throws AccessDeniedException, EntityNotFoundException {

        if (loggedInUser == null || !loggedInUser.isEnabled()) {
            throw new AccessDeniedException("User is either not authenticated or disabled.");
        }

        List<FriendReadOnlyDTO> allUserFriends = friendService.getAllFriendsForUser(loggedInUser.getUsername());

        return new ResponseEntity<>(allUserFriends, HttpStatus.OK);
    }

    @Operation(summary = "Get paginated friends.")
    @GetMapping("/paginated")   // TODO: Improve endpoint names
    public ResponseEntity<Paginated<FriendReadOnlyDTO>> getPaginatedFriends(
            @AuthenticationPrincipal User loggedInUser,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "5") int size
    ) {

        if (loggedInUser == null || !loggedInUser.isEnabled()) {
            throw new AccessDeniedException("User is either not authenticated or disabled.");
        }

        Paginated<FriendReadOnlyDTO> friendsPage = friendService.getPaginatedFriends(pageNo, size, loggedInUser.getId());
        return new ResponseEntity<>(friendsPage, HttpStatus.OK);
    }

    @Operation(summary = "Insert a friend for the authenticated user.")
    @PostMapping
    public ResponseEntity<FriendReadOnlyDTO> insertFriend(
            @AuthenticationPrincipal User loggedInUser,
            @Valid @RequestBody FriendInsertDTO friendInsertDTO,
            BindingResult bindingResult)
            throws ValidationException, EntityAlreadyExistsException, AccessDeniedException, EntityNotFoundException {

        if (loggedInUser == null || !loggedInUser.isEnabled()) {
            throw new AccessDeniedException("User is either not authenticated or disabled.");
        }

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        LOGGER.info("Username {} wants to insert friend with surname {}",
                loggedInUser.getUsername(), friendInsertDTO.getLastname());
        FriendReadOnlyDTO friendReadOnlyDTO = friendService.saveFriend(friendInsertDTO, loggedInUser.getUsername());

        return new ResponseEntity<>(friendReadOnlyDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "Update a friend associated with the authenticated user.")
    @PutMapping("/{id}")
    public ResponseEntity<FriendReadOnlyDTO> updateFriend(
            @AuthenticationPrincipal User loggedInUser,
            @PathVariable String id,
            @Valid @RequestBody FriendUpdateDTO friendUpdateDTO,
            BindingResult bindingResult
            ) throws ValidationException, EntityAlreadyExistsException, EntityInvalidArgumentException,
            EntityNotFoundException, AccessDeniedException {

        if (loggedInUser == null || !loggedInUser.isEnabled()) {
            throw new AccessDeniedException("User is either not authenticated or disabled.");
        }

        if (!id.equals(friendUpdateDTO.getId())) {
            throw new EntityInvalidArgumentException("Friend", "Friend ids do not match.");
        }

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        if (!friendService.existsFriendIdToUsername(Long.parseLong(id), loggedInUser.getUsername())) {
            throw new EntityNotFoundException("Friend", "User " + loggedInUser.getUsername()
                    + " does not have a friend with id " + id);
        }

        FriendReadOnlyDTO friendReadOnlyDTO = friendService.updateFriend(friendUpdateDTO);

        return new ResponseEntity<>(friendReadOnlyDTO, HttpStatus.OK);
    }

    @Operation(summary = "Delete a friend of the authenticated user with the provided id.")
    @DeleteMapping("/{id}")
    public ResponseEntity<FriendReadOnlyDTO> deleteFriendWithId(
            @AuthenticationPrincipal User loggedInUser,
            @PathVariable String id)
            throws EntityNotFoundException, AccessDeniedException {

        if (loggedInUser == null || !loggedInUser.isEnabled()) {
            throw new AccessDeniedException("User is either not authenticated or disabled.");
        }

        if (!friendService.existsFriendIdToUsername(Long.parseLong(id), loggedInUser.getUsername())) {
            throw new EntityNotFoundException("Friend", "User " + loggedInUser.getUsername()
                    + " does not have a friend with id " + id);
        }

        FriendReadOnlyDTO deletedFriendDTO = friendService.deleteFriend(Long.parseLong(id));

        return new ResponseEntity<>(deletedFriendDTO, HttpStatus.OK);
    }
}
