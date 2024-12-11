package mkoutra.birthdaykeeper.dto.userDTOs;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import mkoutra.birthdaykeeper.core.enums.Role;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserInsertDTO {
    @NotEmpty(message = "Username must not be empty.")
    @Size(max = 40, message = "Username must contain less than 40 characters.")
    private String username;

    @NotEmpty(message = "Password must not be empty.")
    @Pattern(regexp = "^(?=.*?[a-z])(?=.*?[A-Z])(?=.*?[0-9])(?=.*?[!@#$%&.+*]).{8,}$",
             message = "Password must be 8+ characters with a mix of uppercase, " +
                       "lowercase, number, and special character !, @, #, $, %, &, ., + or *.")
    private String password;

    @NotNull(message = "Role must not be null.")
    private Role role;
}
