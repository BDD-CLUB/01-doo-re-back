package doore.study.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import doore.study.domain.Study;
import doore.study.domain.StudyStatus;
import doore.team.application.dto.response.TeamReferenceResponse;
import doore.team.domain.Team;
import java.time.LocalDate;
import lombok.Builder;

@Builder
public record StudyResponse(
        Long id,
        String name,
        String description,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate startDate,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate endDate,
        StudyStatus status,
        TeamReferenceResponse teamReference,
        Long cropId,
        long studyProgressRatio,
        Long studyLeaderId

) {
    public static StudyResponse of(final Study study, final Team team, final long studyProgressRatio,
                                   final Long studyLeaderId) {
        return StudyResponse.builder()
                .id(study.getId())
                .name(study.getName())
                .description(study.getDescription())
                .startDate(study.getStartDate())
                .endDate(study.getEndDate())
                .status(study.getStatus())
                .teamReference(TeamReferenceResponse.from(team))
                .cropId(study.getCropId())
                .studyProgressRatio(studyProgressRatio)
                .studyLeaderId(studyLeaderId)
                .build();
    }
}
