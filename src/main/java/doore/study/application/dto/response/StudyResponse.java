package doore.study.application.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import doore.crop.domain.Crop;
import doore.crop.response.CropReferenceResponse;
import doore.member.application.dto.response.MemberReferenceResponse;
import doore.member.domain.Member;
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
        CropReferenceResponse cropReference,
        MemberReferenceResponse memberReference,
        long studyProgressRatio

) {
    public static StudyResponse of(final Study study, final Team team, final Crop crop, final long studyProgressRatio) {
        return StudyResponse.builder()
                .id(study.getId())
                .name(study.getName())
                .description(study.getDescription())
                .startDate(study.getStartDate())
                .endDate(study.getEndDate())
                .status(study.getStatus())
                .teamReference(TeamReferenceResponse.from(team))
                .cropReference(CropReferenceResponse.from(crop))
                .studyProgressRatio(studyProgressRatio)
                .build();
    }

    public static StudyResponse of(final Study study, final Team team, final Crop crop, final Member member,
                                   final long studyProgressRatio) {
        return StudyResponse.builder()
                .id(study.getId())
                .name(study.getName())
                .description(study.getDescription())
                .startDate(study.getStartDate())
                .endDate(study.getEndDate())
                .status(study.getStatus())
                .teamReference(TeamReferenceResponse.from(team))
                .cropReference(CropReferenceResponse.from(crop))
                .memberReference(MemberReferenceResponse.from(member))
                .studyProgressRatio(studyProgressRatio)
                .build();
    }
}
