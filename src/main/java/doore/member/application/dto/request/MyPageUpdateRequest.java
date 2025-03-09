package doore.member.application.dto.request;

import jakarta.validation.constraints.NotBlank;

public record MyPageUpdateRequest(
        @NotBlank(message = "이름을 입력해주세요.")
        String name
) {
}
