package doore.study.application.dto.response;

import doore.study.domain.CurriculumItem;

public record CurriculumItemResponse(
        Long id,
        String name,
        Integer itemOrder,
        Boolean isDeleted
) {

    public static CurriculumItemResponse from(CurriculumItem curriculumItem) {
       return new CurriculumItemResponse(curriculumItem.getId(), curriculumItem.getName(),
                curriculumItem.getItemOrder(), curriculumItem.getIsDeleted());
    }
}
