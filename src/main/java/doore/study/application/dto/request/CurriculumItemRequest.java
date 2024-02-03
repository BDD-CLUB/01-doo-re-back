package doore.study.application.dto.request;

import jakarta.validation.constraints.NotNull;

public record CurriculumItemRequest(
        @NotNull(message = "내용을 입력하세요")
        String name
) {
}

