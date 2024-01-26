package doore.study.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import doore.study.domain.CurriculumItem;
import doore.study.domain.Study;
import doore.study.domain.StudyStatus;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import lombok.Getter;

@Getter
public class StudyDetailResponse {

    Long id;

    String name;

    String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate endTime;

    StudyStatus status;

    Boolean isDeleted;

    Long teamId;

    Long cropId;

    List<CurriculumItemResponse> curriculumItems;

    public StudyDetailResponse(Long id, String name, String description, LocalDate startDate, LocalDate endDate,
                               StudyStatus status, Boolean isDeleted, Long teamId, Long cropId,
                               List<CurriculumItemResponse> curriculumItemResponses) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endTime = endDate;
        this.status =  status;
        this.isDeleted = isDeleted;
        this.teamId = teamId;
        this.cropId = cropId;
        this.curriculumItems = curriculumItemResponses;
    }

    public static StudyDetailResponse from(Study study) {
        List<CurriculumItem> curriculumItems = study.getCurriculumItems();
        List<CurriculumItemResponse> curriculumItemResponses = curriculumItems == null ? Collections.emptyList()
                : curriculumItems.stream().map(CurriculumItemResponse::from)
                        .toList();
        return new StudyDetailResponse(study.getId(), study.getName(), study.getDescription(), study.getStartDate(),
                study.getEndDate(), study.getStatus(), study.getIsDeleted(), study.getTeamId(),
                study.getCropId(), curriculumItemResponses);
    }
}
