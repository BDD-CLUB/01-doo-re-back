package doore.study.application.dto.response.totalStudyResponse;

import com.fasterxml.jackson.annotation.JsonFormat;
import doore.study.application.dto.response.StudyResponse;
import doore.study.domain.StudyStatus;
import jakarta.persistence.Embedded;
import java.time.LocalDate;
import java.util.List;

public record StudyDetailResponse(

        @Embedded
        StudyResponse studyResponse,
        List<CurriculumItemResponse> curriculumItems
) {
}
