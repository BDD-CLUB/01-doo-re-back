package doore.study.application.dto.request;

import doore.study.domain.CurriculumItem;
import doore.study.domain.Study;
import jakarta.validation.constraints.NotNull;

public record CurriculumItemsRequest(
        @NotNull(message = "이름을 입력해주세요.")
        String name,

        @NotNull(message = "커리큘럼 순서를 입력해주세요.")
        Integer itemOrder,

        @NotNull(message = "커리큘럼 삭제여부를 입력해주세요.")
        Boolean isDeleted
) {
}
