package doore.study.application.dto.response.totalStudyResponse;

import doore.study.domain.CurriculumItem;
import doore.study.persistence.dto.StudyOverview;
import java.util.List;

public record CurriculumItemReferenceResponse(
        Long id,
        String name,
        Integer itemOrder,
        Boolean isDeleted
) {
    public static List<CurriculumItemReferenceResponse> from(final List<CurriculumItem> curriculumItems) {
        return curriculumItems.stream()
                .map(curriculumItem -> new CurriculumItemReferenceResponse(
                        curriculumItem.getId(),
                        curriculumItem.getName(),
                        curriculumItem.getItemOrder(),
                        curriculumItem.getIsDeleted()
                ))
                .toList();
    }

    public static List<CurriculumItemReferenceResponse> fromOverview(
            final List<StudyOverview> studyOverviews) {
        return studyOverviews.stream()
                .map(curriculumItem -> new CurriculumItemReferenceResponse(
                        curriculumItem.getCurriculumId(),
                        curriculumItem.getCurriculumName(),
                        curriculumItem.getCurriculumItemOrder(),
                        curriculumItem.getCurriculumItemIsDeleted()
                ))
                .toList();
    }
}
