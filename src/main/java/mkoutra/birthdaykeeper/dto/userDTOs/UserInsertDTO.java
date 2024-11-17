package mkoutra.birthdaykeeper.dto.userDTOs;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserInsertDTO {
    @NotEmpty(message = "Username must not be empty.")
    private String username;

    @NotEmpty(message = "Password must not be empty.")
    private String password;
}
