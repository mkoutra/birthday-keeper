package mkoutra.birthdaykeeper.dto.friendDTOs;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FriendInsertDTO {
    @NotEmpty(message = "First name must not be empty.")
    @Size(max = 40, message = "First name must contain less than 40 characters.")
    private String firstname;

    @NotEmpty(message = "Last name must not be empty.")
    @Size(max = 40, message = "Last name must contain less than 40 characters.")
    private String lastname;

    @Size(max = 40, message = "Nickname must contain less than 40 characters.")
    private String nickname;

    @Past(message = "Birth date must be in the past.")
    private LocalDate dateOfBirth;
}
