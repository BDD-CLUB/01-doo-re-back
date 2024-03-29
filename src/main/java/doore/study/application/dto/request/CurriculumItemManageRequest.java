package doore.study.application.dto.request;

import java.util.List;
import lombok.Builder;

@Builder
public record CurriculumItemManageRequest(
        List<CurriculumItemManageDetailRequest> curriculumItems,
        List<CurriculumItemManageDetailRequest> deletedCurriculumItems
) {
}


