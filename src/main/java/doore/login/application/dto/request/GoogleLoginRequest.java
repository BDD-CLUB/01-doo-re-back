package doore.login.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record GoogleLoginRequest(
        @NotNull(message = "리다이렉션 uri은 null일 수 없습니다.")
        String redirectUri,
        @NotNull(message = "인가 코드는 null일 수 없습니다.")
        String code
) {
}
