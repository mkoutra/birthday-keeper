package mkoutra.birthdaykeeper.dto.userDTOs;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserReadOnlyDTO {
    private String id;
    private String username;
    private String role;
}
