package doore.team.application.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record TeamCreateRequest(
        @NotNull(message = "이름을 입력해주세요.")
        String name,

        @Nullable
        String description
) {
}
