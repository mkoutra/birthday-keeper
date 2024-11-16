package mkoutra.birthdaykeeper.dto.friendsDTOs;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FriendReadOnlyDTO {
    private String uuid;
    private String firstname;
    private String lastname;
    private String nickname;
    private LocalDate dateOfBirth;
}
