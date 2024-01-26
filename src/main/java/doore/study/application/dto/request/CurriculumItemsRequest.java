package doore.study.application.dto.request;

import doore.study.domain.CurriculumItem;
import doore.study.domain.Study;
import jakarta.validation.constraints.NotNull;

public record CurriculumItemsRequest(
        @NotNull(message = "이름을 입력해주세요.")
        String name,
        @NotNull
        Integer itemOrder,
        @NotNull
        Boolean isDeleted
) {
    public CurriculumItem toEntity(Study study) {
        return CurriculumItem.builder()
                .name(this.name())
                .itemOrder(this.itemOrder())
                .isDeleted(this.isDeleted())
                .study(study)
                .build();
    }
}
