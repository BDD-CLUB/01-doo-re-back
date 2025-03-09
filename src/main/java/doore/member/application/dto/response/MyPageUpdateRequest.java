package doore.member.application.dto.response;

import jakarta.validation.constraints.NotNull;

public record MyPageUpdateRequest(
        @NotNull(message = "이름을 입력해주세요.")
        String name
) {
}
