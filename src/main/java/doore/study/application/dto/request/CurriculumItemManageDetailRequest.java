package doore.study.application.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CurriculumItemManageDetailRequest(
        @NotNull(message = "id를 입력해주세요.")
        Long id,
        @NotNull(message = "아이템 순서를 입력해주세요.")
        Integer itemOrder,
        @NotNull(message = "이름을 입력해주세요.")
        String name
) {
}
