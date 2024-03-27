package doore.study.application.dto.response.totalStudyResponse;

import java.util.List;

public record CurriculumItemResponse(
        Long id,
        String name,
        Integer itemOrder,
        Boolean isDeleted,
        List<ParticipantCurriculumItemResponse> participantCurriculumItems
) {
}
