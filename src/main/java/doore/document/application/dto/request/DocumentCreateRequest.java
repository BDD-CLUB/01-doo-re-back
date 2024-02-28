package doore.document.application.dto.request;

import doore.document.domain.DocumentAccessType;
import doore.document.domain.DocumentType;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record DocumentCreateRequest(
        @NotNull(message = "제목을 입력해주세요.")
        String title,

        String description,

        @NotNull(message = "공개범위를 입력해주세요.")
        DocumentAccessType accessType,

        @NotNull(message = "자료 유형을 입력해주세요.")
        DocumentType type,

        List<FileRequest> files,

        @NotNull(message = "업로더 아이디를 입력해주세요.")
        Long uploaderId
) {
}
