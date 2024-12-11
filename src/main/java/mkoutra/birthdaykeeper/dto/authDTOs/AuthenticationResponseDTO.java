package mkoutra.birthdaykeeper.dto.authDTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponseDTO {
    private String username;
    private String jwtToken;    // JWT of the authenticated user.
}
