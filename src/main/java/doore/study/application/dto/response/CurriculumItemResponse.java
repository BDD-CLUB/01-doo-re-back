package doore.study.application.dto.response;

import doore.study.domain.CurriculumItem;
import lombok.Getter;

@Getter
public class CurriculumItemResponse {
    Long id;
    String name;
    Integer itemOrder;
    Boolean isDeleted;

    public CurriculumItemResponse(Long id, String name, Integer itemOrder, Boolean isDeleted) {
        this.id = id;
        this.name = name;
        this.itemOrder = itemOrder;
        this.isDeleted = isDeleted;
    }

    public static CurriculumItemResponse from(CurriculumItem curriculumItem) {
        return new CurriculumItemResponse(curriculumItem.getId(), curriculumItem.getName(),
                curriculumItem.getItemOrder(), curriculumItem.getIsDeleted());
    }
}
