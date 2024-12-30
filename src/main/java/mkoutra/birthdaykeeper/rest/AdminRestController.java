package mkoutra.birthdaykeeper.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import mkoutra.birthdaykeeper.core.Paginated;
import mkoutra.birthdaykeeper.core.exceptions.EntityNotFoundException;
import mkoutra.birthdaykeeper.dto.userDTOs.UserReadOnlyDTO;
import mkoutra.birthdaykeeper.model.User;
import mkoutra.birthdaykeeper.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Administrator Controller",
        description = "Accessible exclusively by ADMIN users, this controller " +
                "provides endpoints for managing user accounts and related operations.")
@SecurityRequirement(name = "jwtAuth")
public class AdminRestController {

    private final UserService userService;

    @Operation(summary = "Get all users of the system.")
    @GetMapping("/users")
    public ResponseEntity<List<UserReadOnlyDTO>> getAllUsers(
            @AuthenticationPrincipal User loggedInUser)
            throws AccessDeniedException {

        if (loggedInUser == null || !loggedInUser.isEnabled()) {
            throw new AccessDeniedException("User is either not authenticated or disabled.");
        }

        List<UserReadOnlyDTO> allUsers = userService.getAllUsers();

        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }

    @Operation(summary = "Get paginated users.")
    @GetMapping("/users/paginated")
    public ResponseEntity<Paginated<UserReadOnlyDTO>> getPaginatedUsers(
            @AuthenticationPrincipal User loggedInUser,
            @RequestParam(defaultValue = "0") int pageNo,
            @RequestParam(defaultValue = "5") int size) {

            if (loggedInUser == null || !loggedInUser.isEnabled()) {
                throw new AccessDeniedException("User is either not authenticated or disabled.");
            }

            Paginated<UserReadOnlyDTO> userPage = userService.getPaginatedUsers(pageNo, size);
            return new ResponseEntity<>(userPage, HttpStatus.OK);
    }

    @Operation(summary = "Delete a user with the provided id.")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<UserReadOnlyDTO> deleteUser(
            @AuthenticationPrincipal User loggedInUser,
            @PathVariable Long id
    ) throws EntityNotFoundException {

        if (loggedInUser == null) {
            throw new AccessDeniedException("User is either not authenticated or disabled.");
        }

        UserReadOnlyDTO deletedUserReadOnlyDTO = userService.deleteUser(id);

        return new ResponseEntity<>(deletedUserReadOnlyDTO, HttpStatus.OK);
    }
}
