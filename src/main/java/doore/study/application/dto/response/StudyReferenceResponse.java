package doore.study.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import doore.study.domain.Study;
import doore.study.domain.StudyStatus;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record StudyReferenceResponse(
        Long id,
        String name,
        String description,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate,
        StudyStatus status,
        Long cropId,
        long studyProgressRatio
) {

    public static StudyReferenceResponse of(final Study study, final long studyProgressRatio) {
        return new StudyReferenceResponse(study.getId(), study.getName(), study.getDescription(), study.getStartDate(),
                study.getEndDate(), study.getStatus(), study.getCropId(), studyProgressRatio);
    }
}
