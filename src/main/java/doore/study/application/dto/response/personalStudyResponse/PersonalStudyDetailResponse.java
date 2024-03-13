package doore.study.application.dto.response.personalStudyResponse;

import doore.study.application.dto.response.StudyResponse;
import jakarta.persistence.Embedded;
import java.util.List;

public record PersonalStudyDetailResponse(
        @Embedded
        StudyResponse studyResponse,
        Long participantId,
        List<PersonalCurriculumItemResponse> PersonalCurriculumItemResponse
) {
}
