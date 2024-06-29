package doore.study.application.dto.response;

import doore.study.domain.Study;
import doore.study.domain.StudyStatus;
import java.time.LocalDate;

public record StudyReferenceResponse(
        Long id,
        String name,
        String description,
        LocalDate startDate,
        LocalDate endDate,
        StudyStatus status,
        Long cropId,
        Long studyProgressRatio
) {

    public static StudyReferenceResponse of(final Study study, final Long studyProgressRatio) {
        return new StudyReferenceResponse(study.getId(), study.getName(), study.getDescription(), study.getStartDate(),
                study.getEndDate(), study.getStatus(), study.getCropId(), studyProgressRatio);
    }
}
