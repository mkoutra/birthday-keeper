package mkoutra.birthdaykeeper.dto.errorDTO;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ErrorResponseDTO {
    private String code;
    private String description;

    public ErrorResponseDTO(String code) {
        this.code = code;
        description = "";
    }
}
