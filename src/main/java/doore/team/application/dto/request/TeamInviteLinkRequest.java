package doore.team.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record TeamInviteLinkRequest(
        @NotBlank(message = "초대링크를 입력해주세요.")
        String link
) {
}
