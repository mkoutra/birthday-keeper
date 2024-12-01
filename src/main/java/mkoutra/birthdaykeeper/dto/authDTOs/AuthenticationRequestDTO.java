package mkoutra.birthdaykeeper.dto.authDTOs;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequestDTO {
    @NotEmpty(message = "Username must not be empty.")
    private String username;

    @NotEmpty(message = "Password must not be empty.")
    private String password;
}
