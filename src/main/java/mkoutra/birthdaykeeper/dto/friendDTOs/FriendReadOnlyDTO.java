package mkoutra.birthdaykeeper.dto.friendDTOs;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FriendReadOnlyDTO {
    private String id;
    private String firstname;
    private String lastname;
    private String nickname;
    private LocalDate dateOfBirth;
    private String daysUntilNextBirthday;
}
