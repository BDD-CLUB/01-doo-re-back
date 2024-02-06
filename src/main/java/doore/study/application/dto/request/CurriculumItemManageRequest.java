package doore.study.application.dto.request;

import doore.study.domain.CurriculumItem;
import java.util.List;
import lombok.Builder;

@Builder
public record CurriculumItemManageRequest(
        List<CurriculumItem> curriculumItems,
        List<CurriculumItem> deletedCurriculumItems
) {
}


