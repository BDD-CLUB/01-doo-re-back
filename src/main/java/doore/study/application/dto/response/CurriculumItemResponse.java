package doore.study.application.dto.response.totalStudyResponse;

import doore.study.domain.CurriculumItem;
import java.util.List;

public record CurriculumItemResponse(
        Long id,
        String name,
        Integer itemOrder,
        Boolean isDeleted,
        List<ParticipantCurriculumItemResponse> participantCurriculumItems
) {

    public static List<CurriculumItemResponse> from(final List<CurriculumItem> curriculumItems) {
        return curriculumItems.stream()
                .map(curriculumItem -> new CurriculumItemResponse(
                        curriculumItem.getId(),
                        curriculumItem.getName(),
                        curriculumItem.getItemOrder(),
                        curriculumItem.getIsDeleted(),
                        ParticipantCurriculumItemResponse.from(curriculumItem.getParticipantCurriculumItems())
                ))
                .toList();
    }
}
