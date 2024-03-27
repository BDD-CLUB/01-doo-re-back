package doore.document.application.dto.request;

import doore.document.domain.DocumentAccessType;
import jakarta.validation.constraints.NotNull;

public record DocumentUpdateRequest(
        @NotNull(message = "제목을 입력해주세요.")
        String title,

        String description,

        @NotNull(message = "공개범위를 입력해주세요.")
        DocumentAccessType accessType
) {
}
