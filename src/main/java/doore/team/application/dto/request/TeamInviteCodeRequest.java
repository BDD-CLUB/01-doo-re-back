package doore.team.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TeamInviteCodeRequest(
        @NotBlank(message = "초대코드를 입력해주세요.")
        String code
) {
}
