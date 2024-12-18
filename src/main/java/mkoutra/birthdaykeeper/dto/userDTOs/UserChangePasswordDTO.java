package mkoutra.birthdaykeeper.dto.userDTOs;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserChangePasswordDTO {
    private String id;
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}
