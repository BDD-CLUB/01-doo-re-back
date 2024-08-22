package doore.study.application.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record CurriculumItemManageDetailRequest(
        Long id,
        @NotNull(message = "아이템 순서를 입력해주세요.")
        @Min(0) @Max(99)
        Integer itemOrder,
        @NotNull(message = "이름을 입력해주세요.")
        String name
) {
}
